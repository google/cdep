package io.cdep.cdep.yml.cdepmanifest;

import io.cdep.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AndroidABI {
  final public static AndroidABI X86 = new AndroidABI("x86");
  final public static AndroidABI X86_64 = new AndroidABI("x86_64");
  final public static AndroidABI ARMEABI = new AndroidABI("armeabi");
  final public static AndroidABI ARMEABI_V7A = new AndroidABI("armeabi-v7a");
  final public static AndroidABI ARM64_V8A = new AndroidABI("arm64-v8a");
  final public static AndroidABI MIPS = new AndroidABI("mips");
  final public static AndroidABI MIPS64 = new AndroidABI("mips64");
  public static final AndroidABI EMPTY_ABI = new AndroidABI("");
  final static private HashSet<AndroidABI> values = new HashSet<>();

  static {
    values.add(X86);
    values.add(X86_64);
    values.add(ARMEABI);
    values.add(ARM64_V8A);
    values.add(ARMEABI_V7A);
    values.add(MIPS);
    values.add(MIPS64);
  }

  @NotNull
  final public String name;

  public AndroidABI(@NotNull String name) {
    this.name = name;
  }

  public static Set<AndroidABI> values() {
    return Collections.unmodifiableSet(values);
  }

  @Override
  public boolean equals(Object obj) {
    return obj != null && obj instanceof AndroidABI && ((AndroidABI) obj).name.equals(name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

  @Override
  public String toString() {
    return name;
  }
}
