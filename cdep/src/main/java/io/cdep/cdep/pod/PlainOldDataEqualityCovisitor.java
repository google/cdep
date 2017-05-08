/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package io.cdep.cdep.pod;

import io.cdep.annotations.Nullable;
import io.cdep.cdep.utils.StringUtils;

import java.util.Objects;

public class PlainOldDataEqualityCovisitor extends PlainOldDataReadonlyCovisitor {

  protected boolean areEqual = true;
  @Nullable
  protected String firstDifference = null;
  @Nullable
  protected Object firstDifferenceLeft = null;
  @Nullable
  protected Object firstDifferenceRight = null;

  public static boolean areDeeplyIdentical(Object left, Object right) {
    PlainOldDataEqualityCovisitor thiz = new PlainOldDataEqualityCovisitor();
    thiz.covisit(left, right);
    return thiz.areEqual;
  }

  protected void checkEquals(Object left, Object right) {
    if (areEqual) {
      boolean equal = Objects.equals(left, right);
      if (!equal) {
        areEqual = false;
        firstDifference = StringUtils.joinOn(".", namestack);
        firstDifferenceLeft = left;
        firstDifferenceRight = right;
      }
    }
  }

  @Override
  public void covisitLong(String name, Long left, Long right) {
    checkEquals(left, right);
  }

  @Override
  public void covisitString(String name, String left, String right) {
    checkEquals(left, right);
  }
}
