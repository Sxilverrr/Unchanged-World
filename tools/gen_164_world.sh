#!/bin/bash
cd "$(dirname "$0")/.."
J8="${JAVA8:-/c/Program Files/Eclipse Adoptium/jdk-8.0.482.8-hotspot/bin/java.exe}"
JAR="$(pwd)/_reference/mc1.6.4/work/minecraft_server.1.6.4.jar"
for seed in "$@"; do
  d="_reference/mc1.6.4/work/server164_$seed"; rm -rf "$d"; mkdir -p "$d"
  echo "eula=true" > "$d/eula.txt"
  printf "level-name=world\nlevel-seed=%s\nonline-mode=false\nsnooper-enabled=false\nspawn-animals=true\nspawn-monsters=true\nspawn-npcs=true\ngenerate-structures=true\n" "$seed" > "$d/server.properties"
  ( cd "$d" && ( while ! grep -q 'Done (' run.log 2>/dev/null; do sleep 2; done; sleep "${SETTLE:-45}"; echo stop; sleep 20 ) | timeout 200 "$J8" -Xmx1G -jar "$JAR" nogui > run.log 2>&1 )
  echo "seed $seed: $(grep -c 'Done (' "$d/run.log") done, $(ls "$d/world/region" | wc -l) region files"
done
