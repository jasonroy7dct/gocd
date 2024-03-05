/*
 * Copyright 2024 Thoughtworks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.server.domain;

import com.thoughtworks.go.server.service.BackupService;
import com.thoughtworks.go.util.command.CommandLine;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostBackupScriptModifiedMockTest {

    @Test
    void shouldCreateCommandLineForSuccessfulBackupInitiatedViaTimer() {
        Date time = new Date(1535551235962L);
        ServerBackup backup = mock(ServerBackup.class);
        when(backup.getPath()).thenReturn("/foo/bar/backupDir/backup_someTimeStamp");
        when(backup.getTime()).thenReturn(time);

        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn("upload-to-s3");
        when(commandLine.getArguments()).thenReturn(Collections.emptyList());
        when(commandLine.getWorkingDirectory()).thenReturn(null);

        Map<String, String> environment = new HashMap<>();
        environment.put("GOCD_BACKUP_STATUS", "success");
        environment.put("GOCD_BACKUP_BASE_DIR", "/foo/bar/backupDir");
        environment.put("GOCD_BACKUP_PATH", "/foo/bar/backupDir/backup_someTimeStamp");
        environment.put("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");
        environment.put("GOCD_BACKUP_INITIATED_VIA", "TIMER");

        when(commandLine.env()).thenReturn(environment);

        PostBackupScript postBackupScript = mock(PostBackupScript.class);
        when(postBackupScript.commandLine()).thenReturn(commandLine);

        CommandLine resultCommandLine = postBackupScript.commandLine();

        assertThat(resultCommandLine.getExecutable()).isEqualTo("upload-to-s3");
        assertThat(resultCommandLine.getArguments()).isEmpty();
        assertThat(resultCommandLine.getWorkingDirectory()).isNull();
        assertThat(resultCommandLine.env())
            .hasSize(5)
            .containsEntry("GOCD_BACKUP_STATUS", "success")
            .containsEntry("GOCD_BACKUP_BASE_DIR", "/foo/bar/backupDir")
            .containsEntry("GOCD_BACKUP_PATH", "/foo/bar/backupDir/backup_someTimeStamp")
            .containsEntry("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z")
            .containsEntry("GOCD_BACKUP_INITIATED_VIA", "TIMER");
    }

    @Test
    void shouldCreateCommandLineForBackupThatErroredWhenInitiatedViaTimer() {
        Date time = new Date(1535551235962L);
        ServerBackup backup = mock(ServerBackup.class);
        when(backup.getPath()).thenReturn("/foo/bar/backupDir/backup_someTimeStamp");
        when(backup.getTime()).thenReturn(time);
        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn("upload-to-s3");
        when(commandLine.getArguments()).thenReturn(Collections.emptyList());
        when(commandLine.getWorkingDirectory()).thenReturn(null);

        Map<String, String> environment = new HashMap<>();
        environment.put("GOCD_BACKUP_STATUS", "failure");
        environment.put("GOCD_BACKUP_INITIATED_VIA", "TIMER");
        environment.put("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");

        when(commandLine.env()).thenReturn(environment);

        PostBackupScript postBackupScript = mock(PostBackupScript.class);
        when(postBackupScript.commandLine()).thenReturn(commandLine);

        CommandLine resultCommandLine = postBackupScript.commandLine();

        assertThat(resultCommandLine.getExecutable()).isEqualTo("upload-to-s3");
        assertThat(resultCommandLine.getArguments()).isEmpty();
        assertThat(resultCommandLine.getWorkingDirectory()).isNull();
        assertThat(resultCommandLine.env())
            .hasSize(3)
            .containsEntry("GOCD_BACKUP_STATUS", "failure")
            .containsEntry("GOCD_BACKUP_INITIATED_VIA", "TIMER")
            .containsEntry("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");
    }

    @Test
    void shouldCreateCommandLineForSuccessfulBackupInitiatedByUser() {
        Date time = new Date(1535551235962L);
        ServerBackup backup = mock(ServerBackup.class);
        when(backup.getPath()).thenReturn("/foo/bar/backupDir/backup_someTimeStamp");
        when(backup.getTime()).thenReturn(time);

        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn("upload-to-s3");
        when(commandLine.getArguments()).thenReturn(Collections.emptyList());
        when(commandLine.getWorkingDirectory()).thenReturn(null);

        Map<String, String> environment = new HashMap<>();
        environment.put("GOCD_BACKUP_STATUS", "success");
        environment.put("GOCD_BACKUP_BASE_DIR", "/foo/bar/backupDir");
        environment.put("GOCD_BACKUP_PATH", "/foo/bar/backupDir/backup_someTimeStamp");
        environment.put("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");
        environment.put("GOCD_BACKUP_INITIATED_BY_USER", "bob");

        when(commandLine.env()).thenReturn(environment);

        PostBackupScript postBackupScript = mock(PostBackupScript.class);
        when(postBackupScript.commandLine()).thenReturn(commandLine);

        CommandLine resultCommandLine = postBackupScript.commandLine();

        assertThat(resultCommandLine.getExecutable()).isEqualTo("upload-to-s3");
        assertThat(resultCommandLine.getArguments()).isEmpty();
        assertThat(resultCommandLine.getWorkingDirectory()).isNull();
        assertThat(resultCommandLine.env())
            .hasSize(5)
            .containsEntry("GOCD_BACKUP_STATUS", "success")
            .containsEntry("GOCD_BACKUP_BASE_DIR", "/foo/bar/backupDir")
            .containsEntry("GOCD_BACKUP_PATH", "/foo/bar/backupDir/backup_someTimeStamp")
            .containsEntry("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z")
            .containsEntry("GOCD_BACKUP_INITIATED_BY_USER", "bob");
    }

    @Test
    void shouldCreateCommandLineForBackupThatErroredWhenInitiatedByUser() {
        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn("upload-to-s3");
        when(commandLine.getArguments()).thenReturn(Collections.emptyList());
        when(commandLine.getWorkingDirectory()).thenReturn(null);

        Map<String, String> environment = new HashMap<>();
        environment.put("GOCD_BACKUP_STATUS", "failure");
        environment.put("GOCD_BACKUP_INITIATED_BY_USER", "bob");
        environment.put("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");

        when(commandLine.env()).thenReturn(environment);

        PostBackupScript postBackupScript = mock(PostBackupScript.class);
        when(postBackupScript.commandLine()).thenReturn(commandLine);

        CommandLine resultCommandLine = postBackupScript.commandLine();

        assertThat(resultCommandLine.getExecutable()).isEqualTo("upload-to-s3");
        assertThat(resultCommandLine.getArguments()).isEmpty();
        assertThat(resultCommandLine.getWorkingDirectory()).isNull();
        assertThat(resultCommandLine.env())
            .hasSize(3)
            .containsEntry("GOCD_BACKUP_STATUS", "failure")
            .containsEntry("GOCD_BACKUP_INITIATED_BY_USER", "bob")
            .containsEntry("GOCD_BACKUP_TIMESTAMP", "2018-08-29T14:00:35Z");
    }

    @Test
    void shouldReturnFalseAndNotBlowUpIfExecutionOfCommandFails() {
        Date time = new Date(1535551235962L);
        ServerBackup backup = mock(ServerBackup.class);
        when(backup.getPath()).thenReturn("/foo/bar/backupDir/backup_someTimeStamp");
        when(backup.getTime()).thenReturn(time);

        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn(RandomStringUtils.randomAlphabetic(32));
        when(commandLine.env()).thenReturn(Collections.emptyMap());

        PostBackupScript postBackupScript = mock(PostBackupScript.class);
        when(postBackupScript.commandLine()).thenReturn(commandLine);
        assertThat(postBackupScript.execute()).isFalse();
    }

    @Test
    void shouldReturnTrueIfExecutionOfCommandSucceeds() {
        Date time = new Date(1535551235962L);
        ServerBackup backup = mock(ServerBackup.class);
        when(backup.getPath()).thenReturn("/foo/bar/backupDir/backup_someTimeStamp");
        when(backup.getTime()).thenReturn(time);

        CommandLine commandLine = mock(CommandLine.class);
        when(commandLine.getExecutable()).thenReturn("jcmd"); // provided by the JDK
        when(commandLine.env()).thenReturn(Collections.emptyMap());

        PostBackupScript postBackupScript = new PostBackupScript("command", BackupService.BackupInitiator.USER, "bob", backup, "/foo/bar/backupDir", time);
        assertThat(postBackupScript.execute()).isTrue();
    }
}
