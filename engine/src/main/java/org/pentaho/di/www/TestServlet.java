/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package org.pentaho.di.www;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.Job;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class TestServlet extends BaseHttpServlet implements CartePluginInterface {
    private static Class<?> PKG = TestServlet.class; // for i18n purposes, needed by Translator2!!
    private static final long serialVersionUID = 1116802722113075758L;
    public static final String CONTEXT_PATH = "/kettle/testJob";


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        Job job = getJobMap().findJob(id);
        response.setContentType("text/json; charset=utf-8");
        PrintWriter out = response.getWriter();
        JSONObject jobInfo = new JSONObject();
        jobInfo.put("jobName", job.getJobname());
        int lastLineNr = KettleLogStore.getLastBufferLineNr();
        if (job.getJobEntryResults() != null) {
            JSONArray jobEntryStatus = new JSONArray();
            job.getJobEntryResults().forEach(entryResult -> {
                JSONObject entryStatus = new JSONObject();
                entryStatus.put("logDate", entryResult.getLogDate());
                entryStatus.put("logChannelId", entryResult.getLogChannelId());
                entryStatus.put("logText", KettleLogStore.getAppender().getBuffer(
                        entryResult.getLogChannelId(), false, 0, lastLineNr).toString());
                entryStatus.put("jobEntryNr", entryResult.getJobEntryNr());
                entryStatus.put("jobEntryName", entryResult.getJobEntryName());
                entryStatus.put("jobEntryFilename", entryResult.getJobEntryFilename());
                entryStatus.put("comment", entryResult.getComment());
                entryStatus.put("reason", entryResult.getReason());
                if (entryResult.getResult() != null) {
                    entryStatus.put("result_result", entryResult.getResult().getResult());
                    entryStatus.put("result_logChannelId", entryResult.getResult().getLogChannelId());
                    entryStatus.put("result_exitStatus", entryResult.getResult().getExitStatus());
                    entryStatus.put("result_logText", entryResult.getResult().getLogText());
                }
                jobEntryStatus.add(entryStatus);
            });
            jobInfo.put("jobEntryStatus", jobEntryStatus);
        }

        out.print(jobInfo); // 输出
        out.flush();
        response.flushBuffer();
    }

    @Override
    public String getContextPath() {
        return CONTEXT_PATH;
    }

    @Override
    public String getService() {
        return CONTEXT_PATH + " (" + toString() + ")";
    }

    public String toString() {
        return "test job";
    }
}
