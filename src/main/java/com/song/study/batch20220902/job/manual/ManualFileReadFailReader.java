package com.song.study.batch20220902.job.manual;

import java.nio.file.Files;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.song.study.batch20220902.job.manual.item.CsvLine;
import com.song.study.batch20220902.job.manual.item.ManualFooter;
import com.song.study.batch20220902.job.manual.item.ManualHeader;
import com.song.study.batch20220902.job.manual.item.ManualItem;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@StepScope
public class ManualFileReadFailReader implements ItemReader<CsvLine>, ItemStream {

    private static final String PREV_READ_COUNT_KEY = "prev_read_count_key";

    private FlatFileItemReader<CsvLine> reader;
    private final StepExecution stepExecution;
    private int linesToSkip = 0;

    public ManualFileReadFailReader(@Value("#{StepExecution}") StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }
    @SneakyThrows
    @Override
    public void open(ExecutionContext executionContext) {

        if (stepExecution.getExecutionContext().containsKey(PREV_READ_COUNT_KEY)) {
            linesToSkip = stepExecution.getExecutionContext().getInt(PREV_READ_COUNT_KEY);
        }
        log.info("[reader] open. read count: {}", linesToSkip);

        ClassPathResource resource = new ClassPathResource("test_manual.csv");
        long lineCount = Files.lines(resource.getFile().toPath()).count();

        reader = new FlatFileItemReaderBuilder<CsvLine>()
            .name("simpleFileReadReader")
            .resource(resource)
            .lineMapper(new ManualLineMapper(lineCount))
            .encoding("UTF-8")
            .linesToSkip(linesToSkip)
            .saveState(true) // ?????? ????????? ????????? ????????? ?????? (?????? ??? ???. ?????? false??? ???????????? ????)
            .build();

        reader.open(executionContext);
        reader.afterPropertiesSet();
    }

    @Override
    public CsvLine read() throws Exception {
        CsvLine read = reader.read();
        logManualLine(read);
        return read;
    }

    private void logManualLine(Object item) {
        if (item instanceof ManualHeader header) {
            log.info("[reader] header check: {}", header.getDescription());
        }

        if (item instanceof ManualItem manualItem) {
            log.info("[reader] body: {}, {}", manualItem.getName(), manualItem.getAge());
        }

        if (item instanceof ManualFooter footer) {
            log.info("[reader] footer count: {}", footer.getCount());
        }
    }

    @Override
    public void update(ExecutionContext executionContext) {
        log.info("[reader] update.");
        executionContext.putInt(PREV_READ_COUNT_KEY, linesToSkip + stepExecution.getReadCount());
    }

    @Override
    public void close() {
        log.info("[reader] close");
        reader.close();
    }

    private static final class ManualLineMapper implements LineMapper<CsvLine> {

        private final long totalLineCount;

        private ManualLineMapper(long totalLineCount) {
            this.totalLineCount = totalLineCount;
        }

        @Override
        public CsvLine mapLine(String line, int lineNumber) throws Exception {
            if (lineNumber == 1) {
                return ManualHeader.builder().description(line).build();
            }

//            if (lineNumber == 5) {
//                throw new RuntimeException("TEST");
//            }

            if (lineNumber >= totalLineCount) {
                String count = line.replace("count: ", "");
                return ManualFooter.builder().count(Long.valueOf(count)).build();
            }

            String[] split = line.split(",");

            for (String value : split) {
                log.info("mapLine: {}", value);
            }

            return ManualItem.builder().name(split[0]).age(Long.valueOf(split[1])).build();
        }
    }
}
