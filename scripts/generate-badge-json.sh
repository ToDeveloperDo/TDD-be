#!/bin/bash

mkdir -p badges
REPORT="build/reports/jacoco/test/jacocoTestReport.xml"
OUTPUT="badges/coverage-badge.json"

LINE_COVERED=$(grep -o 'covered="[0-9]*"' "$REPORT" | cut -d'"' -f2 | paste -sd+ - | bc)
LINE_MISSED=$(grep -o 'missed="[0-9]*"' "$REPORT" | cut -d'"' -f2 | paste -sd+ - | bc)
TOTAL=$((LINE_COVERED + LINE_MISSED))

if [ "$TOTAL" -eq 0 ]; then
  PERCENT=0
else
  PERCENT=$(echo "scale=2; 100 * $LINE_COVERED / $TOTAL" | bc)
fi

COLOR=red
if (( $(echo "$PERCENT > 90" | bc -l) )); then
  COLOR=brightgreen
elif (( $(echo "$PERCENT > 75" | bc -l) )); then
  COLOR=yellow
fi

cat <<EOF > "$OUTPUT"
{
  "schemaVersion": 1,
  "label": "Test Coverage",
  "message": "$PERCENT%",
  "color": "$COLOR"
}
EOF
