package io.cdep.cdep.yml.cdeparchive;

import io.cdep.annotations.NotNull;

/**
 * Record information about a single archive (.zip) in the local file system.
 */
public class CDepArchiveYml {
  final public String coordinate;
  final public String remote;
  final public String sha256;
  final public Long size;

  public CDepArchiveYml() {
    this.coordinate = null;
    this.remote = null;
    this.sha256 = null;
    this.size = null;
  }

  public CDepArchiveYml(String coordinate, String remote, String sha256, Long size) {
    this.coordinate = coordinate;
    this.remote = remote;
    this.sha256 = sha256;
    this.size = size;
  }

  private String toYaml(int indent) {
    String prefix = new String(new char[indent * 2]).replace('\0', ' ');
    StringBuilder sb = new StringBuilder();
    if (coordinate != null && coordinate.length() > 0) {
      sb.append(String.format("%scoordinate: %s\r\n", prefix, coordinate));
    }
    if (remote != null && remote.length() > 0) {
      sb.append(String.format("%sremote: %s\r\n", prefix, remote));
    }
    if (sha256 != null && sha256.length() > 0) {
      sb.append(String.format("%ssha256: %s\r\n", prefix, sha256));
    }
    if (size != null) {
      sb.append(String.format("%ssize: %s\r\n", prefix, size));
    }
    return sb.toString();
  }

  @NotNull
  @Override
  public String toString() {
    return toYaml(0);
  }
}
