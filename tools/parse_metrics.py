#!/usr/bin/env python3
"""
Parse Android logcat output to extract and aggregate VideoStartMetrics.

Usage:
    adb logcat -d -s MetricsCSV:D | python parse_metrics.py
    adb logcat -d -s MetricsCSV:D > logcat.txt && python parse_metrics.py logcat.txt
"""
import re
import sys
import json
from collections import defaultdict
from datetime import datetime

CSV_PATTERN = re.compile(
    r'MetricsCSV.*?([\w_]+),cold=(\d+),preload=(\d+),hit=(true|false),improve=(\d+)%'
)


def parse_logcat(text: str) -> list[dict]:
    records = []
    for line in text.splitlines():
        m = CSV_PATTERN.search(line)
        if m:
            records.append({
                "videoId": m.group(1),
                "coldMs": int(m.group(2)),
                "preloadMs": int(m.group(3)),
                "isPreload": m.group(4) == "true",
                "improvePct": int(m.group(5)),
            })
    return records


def aggregate(records: list[dict]) -> dict:
    if not records:
        return {"error": "No metrics found in input"}

    cold = [r for r in records if not r["isPreload"]]
    preload = [r for r in records if r["isPreload"]]

    def avg(lst, key):
        return round(sum(r[key] for r in lst) / len(lst), 1) if lst else 0

    return {
        "timestamp": datetime.now().isoformat(),
        "totalMeasurements": len(records),
        "coldStartCount": len(cold),
        "preloadHitCount": len(preload),
        "preloadHitRatePct": round(len(preload) / len(records) * 100, 1),
        "avgColdStartMs": avg(cold, "coldMs"),
        "avgPreloadMs": avg(preload, "preloadMs"),
        "avgImprovementPct": round(sum(r["improvePct"] for r in records) / len(records), 1),
        "bestImprovementPct": max(r["improvePct"] for r in records),
        "worstColdStartMs": max((r["coldMs"] for r in cold), default=0),
        "bestColdStartMs": min((r["coldMs"] for r in cold), default=0),
        "details": records,
    }


def main():
    if len(sys.argv) > 1:
        with open(sys.argv[1]) as f:
            text = f.read()
    else:
        text = sys.stdin.read()

    records = parse_logcat(text)
    result = aggregate(records)
    print(json.dumps(result, indent=2, ensure_ascii=False))
    return 0 if records else 1


if __name__ == "__main__":
    sys.exit(main())
