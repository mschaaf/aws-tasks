/**
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datameer.awstasks.ant.ec2;

import org.apache.tools.ant.BuildException;

public class Ec2StartRetryTask extends Ec2StartTask {

    private int _retryCount = 3;

    public int getRetryCount() {
        return _retryCount;
    }

    public void setRetryCount(int retryCount) {
        _retryCount = retryCount;
    }

    @Override
    public void doExecute() throws BuildException {
        int tryCount = 0;
        do {
            try {
                tryCount++;
                super.doExecute();
                return;
            } catch (BuildException startException) {
                LOG.warn("failed to start '" + _groupName + "' in try " + tryCount);
                try {
                    Ec2StopTask stopTask = new Ec2StopTask();
                    stopTask.setAccessKey(_accessKey);
                    stopTask.setAccessSecret(_accessSecret);
                    stopTask.setGroupName(_groupName);
                    stopTask.execute();
                } catch (Exception stopException) {
                    throw startException;
                }
            }

        } while (tryCount < _retryCount);
    }
}
