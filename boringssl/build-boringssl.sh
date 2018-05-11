BORINGSSL_STAMP=51776b0aebcd658c0f8867eaf5bce5ac80afb4df

curl -L -o boringssl.zip https://github.com/google/boringssl/archive/${BORINGSSL_STAMP}.zip
unzip boringssl.zip > boringssl-unzip.log

echo cd boringssl-${BORINGSSL_STAMP}
cd boringssl-${BORINGSSL_STAMP}
cp ../cmakeify-ssl.yml cmakeify.yml
../../cmakeify --group-id com.github.google --artifact-id boringssl/ssl --target-version 0.0.0
cp ../cmakeify-crypto.yml cmakeify.yml
../../cmakeify --group-id com.github.google --artifact-id boringssl/crypto --target-version 0.0.0
