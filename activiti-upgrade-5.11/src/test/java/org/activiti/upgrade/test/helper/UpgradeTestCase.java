package org.activiti.upgrade.test.helper;

import org.activiti.engine.test.ActivitiTestCase;
import org.activiti.upgrade.helper.ActivitiVersion;
import org.activiti.upgrade.helper.UpgradeUtil;
import org.junit.Assume;
import org.junit.Ignore;

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

/**
 * @author Joram Barrez
 */
@Ignore
public class UpgradeTestCase extends ActivitiTestCase {
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    
    // verify if there is an minimal version requirement on the test
    MinimalOldVersion minimalOldVersion = this.getClass().getAnnotation(MinimalOldVersion.class);
    if (minimalOldVersion != null) {
      ActivitiVersion minimalVersion = new ActivitiVersion(minimalOldVersion.value());
      ActivitiVersion oldVersion = UpgradeUtil.getOldVersion(); // This is the version against which the data was generated
      Assume.assumeTrue(oldVersion.compareTo(minimalVersion) >= 0);
    }
  }
  
  @Override
  protected void initializeProcessEngine() {
    this.processEngine = UpgradeUtil.getProcessEngine(getConfigurationResource());
  }
  
}
