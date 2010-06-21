/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.test.bpmn.gateway;

import static org.junit.Assert.assertEquals;

import org.activiti.ActivitiException;
import org.activiti.ProcessInstance;
import org.activiti.test.ActivitiTestCase;
import org.activiti.test.ProcessDeclared;
import org.activiti.util.CollectionUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * @author Joram Barrez
 */
public class ExclusiveGatewayTest extends ActivitiTestCase {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  @ProcessDeclared
  public void testDivergingExclusiveGateway() {
    for (int i = 1; i <= 3; i++) {
      ProcessInstance pi = processService.startProcessInstanceByKey("exclusiveGwDiverging", CollectionUtil.singletonMap("input", i));
      assertEquals("Task " + i, taskService.createTaskQuery().singleResult().getName());
      processService.deleteProcessInstance(pi.getId());
    }
  }

  @Test
  @ProcessDeclared
  public void testMergingExclusiveGateway() {
    processService.startProcessInstanceByKey("exclusiveGwMerging");
    assertEquals(3, taskService.createTaskQuery().count());
  }

  // If there are multiple outgoing seqFlow with valid conditions, the first
  // defined one should be chosen.
  @Test
  @ProcessDeclared
  public void testMultipleValidConditions() {
    processService.startProcessInstanceByKey("exclusiveGwMultipleValidConditions", CollectionUtil.singletonMap("input", 5));
    assertEquals("Task 2", taskService.createTaskQuery().singleResult().getName());
  }

  @Test
  @ProcessDeclared
  public void testNoSequenceFlowSelected() {
    exception.expect(ActivitiException.class);
    exception.expectMessage("No outgoing sequence flow of the exclusive gateway " + "'exclusiveGw' could be selected for continuing the process");
    processService.startProcessInstanceByKey("exclusiveGwNoSeqFlowSelected", CollectionUtil.singletonMap("input", 4));
  }

}
