BORINGSSL_STAMP=51776b0aebcd658c0f8867eaf5bce5ac80afb4df

curl -L -o boringssl.zip https://github.com/google/boringssl/archive/${BORINGSSL_STAMP}.zip
unzip boringssl.zip > boringssl-unzip.log

cd boringssl-${BORINGSSL_STAMP}
cp ../cmakeify.yml .
../../cmakeify --group-id com.github.google --artifact-id boringssl --target-version 0.0.0
