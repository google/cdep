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
package io.cdep.cdep;

import static io.cdep.cdep.ast.finder.ExpressionBuilder.abort;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.assign;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.assignmentBlock;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.constant;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.gte;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.ifSwitch;
import static io.cdep.cdep.ast.finder.ExpressionBuilder.reference;

import io.cdep.cdep.ast.finder.AssignmentExpression;
import org.junit.Test;

public class TestCheckLocalFileSystemIntegrity {

  @Test
  public void testUnreferencedAssignment() {
    // This is an assignment that is used within an if-statement.
    // CheckLocalFilesystemIntegrity should not evaluate if-conditions
    AssignmentExpression assignment1 = assign("assignment1", gte(constant(1), 2));
    AssignmentExpression assignment2 = assign("assignment2", reference(assignment1));
    new CheckLocalFileSystemIntegrity(null).visit(assignmentBlock(assignment2, ifSwitch(reference(assignment2), abort("true"),
        abort("false"))));
  }
}
