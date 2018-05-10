BORINGSSL_STAMP=51776b0aebcd658c0f8867eaf5bce5ac80afb4df
CMAKE_BIN_FOLDER=${ANDROID_HOME}/cmake/3.6.4111459/bin
LAYOUT_FOLDER=${PWD}/layout

# curl -L -o boringssl.zip https://github.com/google/boringssl/archive/${BORINGSSL_STAMP}.zip
# unzip boringssl.zip > boringssl-unzip.log
mkdir -p ${LAYOUT_FOLDER}/lib

cd boringssl-${BORINGSSL_STAMP}
build() {
  rm -rf build/${ABI}
  mkdir -p build/${ABI}
  cd build/${ABI}
  ${CMAKE_BIN_FOLDER}/cmake -DANDROID_ABI=${ABI} \
      -DCMAKE_TOOLCHAIN_FILE=${ANDROID_NDK_HOME}/build/cmake/android.toolchain.cmake \
      -DANDROID_NATIVE_API_LEVEL=21 \
      -DCMAKE_MAKE_PROGRAM=${CMAKE_BIN_FOLDER}/ninja \
      -GNinja ../..
  ${CMAKE_BIN_FOLDER}/cmake --build . --target crypto
  ${CMAKE_BIN_FOLDER}/cmake --build . --target ssl
  mkdir -p ${LAYOUT_FOLDER}/lib/${ABI}
  cp crypto/libcrypto.a ${LAYOUT_FOLDER}/lib/${ABI}
  cp ssl/libssl.a ${LAYOUT_FOLDER}/lib/${ABI}
  cd ../..
}

ABI=x86 build
ABI=x86_64 build
ABI=armeabi-v7a build
ABI=arm64-v8a build
