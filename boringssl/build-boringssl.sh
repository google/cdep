BORINGSSL_STAMP=51776b0aebcd658c0f8867eaf5bce5ac80afb4df
BORINGSSL_URL=https://github.com/google/boringssl/archive/${BORINGSSL_STAMP}.zip
curl -L -o boringssl-sources.zip ${BORINGSSL_URL}
unzip boringssl-sources.zip > boringssl-unzip.log
echo ${BORINGSSL_URL} > boringssl-sources-url.txt

echo cd boringssl-${BORINGSSL_STAMP}
cd boringssl-${BORINGSSL_STAMP}
mkdir -p build/zips/boringssl
mkdir -p build/Android
cp ../cmakeify.yml cmakeify.yml
cp ../cdep .
chmod +x cdep
../../cmakeify --group-id com.github.google --artifact-id cdep/boringssl --target-version 0.0.0
cp build/zips/* ..

