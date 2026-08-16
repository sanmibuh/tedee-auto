import os
import json
import urllib.request
import urllib.error
import sys

if len(sys.argv) < 2:
  print("Usage: gemini_review.py <model>", file=sys.stderr)
  sys.exit(1)

model = sys.argv[1]

api_key = os.getenv("GEMINI_API_KEY")
if not api_key:
  print("Error: GEMINI_API_KEY environment variable is not set.", file=sys.stderr)
  sys.exit(1)

with open("code_snapshot.txt") as f:
  code = f.read()

with open("open_issues.json") as f:
  open_issues = json.load(f)

if open_issues:
  open_issues_text = "\n".join(
    f"- #{i['number']}: {i['title']}\n  {(i['body'] or '')[:300]}"
    for i in open_issues
  )
  open_issues_section = (
    "\nALREADY REPORTED (open issues — do NOT create findings for these):\n"
    + open_issues_text
    + "\n"
  )
else:
  open_issues_section = ""

prompt = f"""You are a senior software engineer performing a nightly automated code review.
The project guidelines and quality standards are defined in AGENTS.md (included below).
The architecture is described in ARCHITECTURE.md (included below).
Use both as the reference for this review — do not apply standards from outside these documents.
{open_issues_section}
Analyze the codebase and identify concrete, actionable findings across these areas:
- Clean Code & Best Practices (naming, dead code, DRY, method size, magic values)
- Test Quality / TDD (behavior testing, naming convention, BDDAssertions, missing cases)
- SOLID & Architecture (layer dependencies, domain isolation, SOLID violations)
- Documentation (ARCHITECTURE.md accuracy, comments that restate code)

Rules:
- Each finding must be specific: reference actual class names and method names.
- Do NOT report findings already listed in "ALREADY REPORTED" above.
- Do NOT invent problems. If the code is clean in an area, skip it.
- Return ONLY a JSON array. No explanation outside the JSON.

Each finding must have:
- "title": short descriptive title (max 80 chars), prefixed with the area: [Clean Code], [TDD], [SOLID], or [Docs]
- "priority": "high", "medium", or "low"
- "body": full markdown description with the problem, why it matters, and a concrete suggestion (with code example if useful)

Example format:
[
  {{
    "title": "[TDD] Missing edge case in HandlerLookupTest for duplicate handler registration",
    "priority": "medium",
    "body": "## Problem\\n...\\n## Suggestion\\n..."
  }}
]

If there are no new findings, return an empty array: []

CODEBASE AND GUIDELINES:
{code}
"""

payload = {
  "contents": [{"parts": [{"text": prompt}]}],
  "generationConfig": {
    "temperature": 0.2,
    "maxOutputTokens": 8192,
    "responseMimeType": "application/json"
  }
}

req = urllib.request.Request(
  f"https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={api_key}",
  data=json.dumps(payload).encode(),
  headers={"Content-Type": "application/json"},
  method="POST"
)
try:
  with urllib.request.urlopen(req, timeout=120) as resp:
    data = json.loads(resp.read())
except urllib.error.HTTPError as e:
  if e.code in (404, 429, 503):
    reasons = {
      404: "not available",
      429: "quota exceeded",
      503: "temporarily unavailable (high demand)",
    }
    print(f"Model {model} {reasons[e.code]}, will try next model", file=sys.stderr)
    sys.exit(2)
  print(f"Gemini API error: {e.code} {e.read().decode()}", file=sys.stderr)
  sys.exit(1)
except urllib.error.URLError as e:
  print(f"Network error calling Gemini API: {e.reason}", file=sys.stderr)
  sys.exit(1)

candidates = data.get("candidates", [])
if not candidates or not candidates[0].get("content", {}).get("parts"):
  finish = candidates[0].get("finishReason", "UNKNOWN") if candidates else "NO_CANDIDATES"
  print(f"Gemini returned no content (model={model}). finishReason: {finish}", file=sys.stderr)
  sys.exit(1)

raw = candidates[0]["content"]["parts"][0]["text"]
try:
  findings = json.loads(raw)
except json.JSONDecodeError as e:
  print(f"Failed to parse Gemini response as JSON: {e}\nRaw: {raw[:500]}", file=sys.stderr)
  sys.exit(1)

if not isinstance(findings, list):
  print(f"Gemini response is not a JSON array. Got: {type(findings).__name__}", file=sys.stderr)
  sys.exit(1)

with open("findings.json", "w") as f:
  json.dump({"model": model, "findings": findings}, f)

print(f"Found {len(findings)} new finding(s) with {model}")
