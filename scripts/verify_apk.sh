#!/usr/bin/env bash
# Checks a built Radio Dash APK: aligned, the radio's package, the version from VERSION in the manifest, the helper
# module in the dex, and with --cert, signed (v1 and v2) by that platform certificate.
# Usage: scripts/verify_apk.sh APK [--cert platform.x509.pem]
set -euo pipefail
root=$(cd "$(dirname "$0")/.." && pwd)
apk=$1; shift
cert=""
if [ "${1:-}" = "--cert" ]; then cert=$2; fi
bt=${BUILD_TOOLS:-$(ls -d "$ANDROID_HOME"/build-tools/* | sort -V | tail -1)}
version=$(tr -d '[:space:]' < "$root/VERSION")
fail() { echo "verify_apk: $*" >&2; exit 1; }

"$bt/zipalign" -c -p 4 "$apk" || fail "not aligned"
manifest=$("$bt/aapt2" dump xmltree --file AndroidManifest.xml "$apk")
grep -q 'package="com.saicmotor.radio"' <<<"$manifest" || fail "package is not com.saicmotor.radio"
printf '%s' "$manifest" | python3 -c '
import re, sys
text = sys.stdin.read()
m = re.search(r"android:name\([0-9a-fx]+\)=\"radiodash\.version\".*?\n\s*A: \S*android:value\([0-9a-fx]+\)=\"([^\"]*)\"", text, re.S)
sys.exit(0 if m and m.group(1) == sys.argv[1] else 1)
' "$version" || fail "radiodash.version is not $version"
dex=$(mktemp); trap 'rm -f "$dex"' EXIT
unzip -p "$apk" classes.dex > "$dex"
grep -aq 'Lapp/mg4dash/radiodash/RadioDash;' "$dex" || fail "helper module missing from classes.dex"
if [ -n "$cert" ]; then
  out=$("$bt/apksigner" verify --verbose --print-certs "$apk") || fail "signature does not verify"
  grep -q 'Verified using v1 scheme (JAR signing): true' <<<"$out" || fail "no v1 signature"
  grep -q 'Verified using v2 scheme (APK Signature Scheme v2): true' <<<"$out" || fail "no v2 signature"
  want=$(openssl x509 -in "$cert" -outform der | sha256sum | cut -d' ' -f1)
  grep -qi "certificate SHA-256 digest: $want" <<<"$out" || fail "not signed with the platform certificate"
fi
echo "verify_apk: $(basename "$apk") OK (Radio Dash $version, $(stat -c %s "$apk") bytes)"
