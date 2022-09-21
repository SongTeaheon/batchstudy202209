package com.song.study.batch20220902.job.file;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.core.io.FileSystemResource;

public class FileMoveTasklet implements Tasklet {

    private final File originFile;
    private final File toFile;

    public FileMoveTasklet(String originalFilePath, String pathTo) {
        originFile = new FileSystemResource(originalFilePath).getFile();
        toFile = new File(pathTo);
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        if (!toFile.exists()) {
            boolean isSuccess = toFile.mkdirs();

            if (!isSuccess) {
                throw new RuntimeException("fail to create dir");
            }
        }

        Files.copy(originFile.toPath(), toFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return RepeatStatus.FINISHED;
    }
}
