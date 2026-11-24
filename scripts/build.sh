#!/usr/bin/env bash
# Radio Dash, Linux twin of build.ps1 (used by GitHub Actions, same steps and same tools):
#  1. compiles the helper module (helper/src, Java) with every javac warning as an error, then to smali;
#  2. decodes apk/Radio_patched_signed.apk with apktool;
#  3. lays dash/ (resources, modified smali) and the helper's smali over it, version from VERSION;
#  4. rebuilds and aligns: dist/Radio_Dash_unsigned.apk, then signs it when a key is given.
# Usage: scripts/build.sh [--key platform.pk8 --cert platform.x509.pem]
# Needs: JDK 17, ANDROID_HOME with platforms/android-28 and a build-tools, APKTOOL (apktool 2.10.0 jar).
set -euo pipefail
root=$(cd "$(dirname "$0")/.." && pwd)
key="" cert=""
while [ $# -gt 0 ]; do
  case "$1" in
    --key) key=$2; shift 2 ;;
    --cert) cert=$2; shift 2 ;;
    *) echo "Unknown argument: $1" >&2; exit 2 ;;
  esac
done
: "${ANDROID_HOME:?ANDROID_HOME is not set}"
: "${APKTOOL:?APKTOOL must point to apktool 2.10.0}"
bt=${BUILD_TOOLS:-$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)}
android_jar="$ANDROID_HOME/platforms/android-28/android.jar"
test -f "$android_jar" || { echo "Missing $android_jar" >&2; exit 1; }
version=$(tr -d '[:space:]' < "$root/VERSION")
work="$root/build"
rm -rf "$work" && mkdir -p "$work" "$root/dist"

# 1. Helper module → dex → smali
h="$work/helper"
mkdir -p "$h/classes" "$h/dex"
find "$root/helper/src" -name '*.java' > "$h/sources.txt"
javac --release 8 -encoding UTF-8 -Xlint:all -Xlint:-options -Werror -cp "$android_jar" -d "$h/classes" @"$h/sources.txt"
(cd "$h/classes" && zip -qr "$h/classes.jar" .)
"$bt/d8" --min-api 28 --lib "$android_jar" --output "$h/dex" "$h/classes.jar"
(cd "$h/dex" && zip -q "$h/helper.apk" classes.dex)
java -jar "$APKTOOL" d -r -f -o "$h/decoded" "$h/helper.apk" > /dev/null

# 2. The DAB+ patched radio, decoded
app="$work/radio_src"
java -jar "$APKTOOL" d -f -o "$app" "$root/apk/Radio_patched_signed.apk" > /dev/null

# 3. Overlay, then Radio Dash's version in <application> (meta-data "radiodash.version", read by MG4Dash).
# The versionName stays the original radio's (MG4Dash uses it for compatibility).
cp -a "$root/dash/." "$app/"
python3 - "$app/AndroidManifest.xml" "$version" <<'PY'
import re, sys
path, version = sys.argv[1], sys.argv[2]
m = open(path, encoding='utf-8').read()
m = re.sub(r'<meta-data android:name="radiodash\.version"[^>]*/>\s*', '', m)
meta = '<meta-data android:name="radiodash.version" android:value="%s"/>' % version
m, n = re.subn(r'(<application\b[^>]*[^/]>)', lambda g: g.group(1) + '\n        ' + meta, m, count=1)
if n != 1:
    sys.exit('meta-data radiodash.version not added to the manifest')
open(path, 'w', encoding='utf-8').write(m)
PY
echo "Radio Dash version: $version"
mkdir -p "$app/smali/app/mg4dash/radiodash"
cp "$h/decoded/smali/app/mg4dash/radiodash/"*.smali "$app/smali/app/mg4dash/radiodash/"

# 4. Rebuild, align, sign
java -jar "$APKTOOL" b -o "$work/radio_unsigned.apk" "$app"
"$bt/zipalign" -f -p 4 "$work/radio_unsigned.apk" "$root/dist/Radio_Dash_unsigned.apk"
echo "APK: dist/Radio_Dash_unsigned.apk"
if [ -n "$key" ]; then
  "$bt/apksigner" sign --key "$key" --cert "$cert" --v1-signing-enabled true --v2-signing-enabled true \
    --out "$root/dist/Radio_Dash_signed.apk" "$root/dist/Radio_Dash_unsigned.apk"
  echo "APK: dist/Radio_Dash_signed.apk"
fi
