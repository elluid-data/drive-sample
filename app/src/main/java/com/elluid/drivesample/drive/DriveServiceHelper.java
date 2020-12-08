package com.elluid.drivesample.drive;

/**
 * Copyright 2018 Google LLC
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
import android.app.Activity;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DriveServiceHelper {
    private final Executor mExecutor = Executors.newSingleThreadExecutor();
    private final Drive mDriveService;

    private DriveServiceHelper(Drive driveService) {
        mDriveService = driveService;
    }

    public Task<String> sendFile(String name, byte[] bytes) {
        return Tasks.call(mExecutor, () -> {
            File metadata = new File().setName(name);
            metadata.setName(name);
            ByteArrayContent contentStream = new ByteArrayContent("text/plain", bytes);
            mDriveService.files().create(metadata, contentStream).execute();
            return name;
        });
    }

    public static DriveServiceHelper getDriveServiceHelper(GoogleSignInAccount googleSignInAccount, Activity activity) {
        GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                activity, Collections.singleton(DriveScopes.DRIVE_FILE));
        credential.setSelectedAccount(googleSignInAccount.getAccount());
        Drive googleDriveService = new Drive.Builder(new NetHttpTransport.Builder().build(),
                new GsonFactory(), credential)
                .setApplicationName("Drive Sample")
                .build();
        return new DriveServiceHelper(googleDriveService);
    }
}