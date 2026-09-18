#!/bin/bash
cd "$(dirname "$0")/.."
export JAVA_HOME="${JAVA_HOME:-C:/Users/User/.gradle/jdks/eclipse_adoptium-25-amd64-windows.2}"
SEED="$1"; NAME="$2"; REF="$3"
mkdir -p build/worldtest
LOG="build/worldtest/runserver_$NAME.log"
: > "$LOG"
sed -i -e "s/^level-seed=.*/level-seed=$SEED/" -e "s/^level-name=.*/level-name=$NAME/" run/server/server.properties
rm -rf "run/server/$NAME"
( while ! grep -q 'Done (\|BUILD FAILED\|FAILURE:' "$LOG"; do sleep 3; done
  if grep -q 'Done (' "$LOG"; then sleep "${SETTLE:-45}"; echo stop; sleep 40; fi ) | timeout 500 ./gradlew runServer --console=plain >> "$LOG" 2>&1
grep -n "Registered world type\|Done (\|BUILD FAILED\|Stopping server\|Exception in\|FATAL\|MixinApplyError\|InjectionError" "$LOG" | head -12
if [ -n "$REF" ]; then
  echo "=== compare $NAME vs $REF"
  py -3 tools/compare_worlds.py "$REF" "run/server/$NAME"
  py -3 tools/diagnostics/diff_grazing.py "$REF" "run/server/$NAME" | grep "entities\|total"
fi
