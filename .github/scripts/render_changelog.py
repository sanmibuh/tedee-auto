#!/usr/bin/env python3
"""Render a single CHANGELOG entry from /tmp/prs.json.

Reads the merged-PR list produced by the workflow (a JSON array of
{title, number, url} objects) and prints one Markdown changelog section:

    ## [VERSION] - TODAY

    ### <category>
    - <title> ([#<number>](<url>))

Environment:
    VERSION  release version, e.g. "1.4.0"  (required)
    TODAY    ISO date, e.g. "2026-09-18"     (required)
    PRS_FILE path to the PR JSON             (required)

The output is the authoritative source for both the release-PR preview
(release.yml) and the published CHANGELOG.md / release notes (publish.yml),
so both always categorise and format identically.
"""

import json
import os

version = os.environ["VERSION"]
today = os.environ["TODAY"]
prs_file = os.environ["PRS_FILE"]

with open(prs_file) as f:
    prs = json.load(f)

# Ordered category definitions: (heading, matcher on lowercased title).
CATEGORIES = [
    ("Features", lambda t: t.startswith(("feat", "[feature]"))),
    ("Bug Fixes", lambda t: t.startswith(("fix", "[fix]")) or "fix " in t[:12]),
    ("Code Quality", lambda t: t.startswith(("[refactor]", "[clean code]", "[solid]", "[tdd]", "refactor"))),
    ("Dependencies", lambda t: t.startswith(("build(deps", "build(deps-dev")) or "bump " in t),
    ("Build & CI", lambda t: t.startswith(("build", "ci", "chore"))),
]


def categorize(title):
    low = title.lower()
    for heading, matches in CATEGORIES:
        if matches(low):
            return heading
    return "Other"


groups = {}
for pr in prs:
    groups.setdefault(categorize(pr["title"]), []).append(pr)

lines = [f"## [{version}] - {today}"]
if prs:
    order = [h for h, _ in CATEGORIES] + ["Other"]
    for heading in order:
        items = groups.get(heading)
        if not items:
            continue
        lines.append("")
        lines.append(f"### {heading}")
        for pr in items:
            lines.append(f"- {pr['title']} ([#{pr['number']}]({pr['url']}))")
else:
    lines.append("")
    lines.append("_No changes._")

print("\n".join(lines))
