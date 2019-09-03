package ru.otus.lesson9;

import java.io.*;

/**
 * Created by Alexander Bryantsev on 07.08.2019.
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {

        String javaPath = "c:\\app\\java\\jdk-12.0.2\\bin\\java"; // Путь к JDK
        // Путь к JDK для запуска тестирования может быть передан в виде параметра
        if (args != null && args.length == 1) {
            javaPath = args[0];
        }

        final String jvmOptions =
            " --enable-preview -Xms5120m -Xmx5120m " +
                // " -Xlog:gc=debug:file=./logs/gc-<gc>-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m " +
                //" -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/<gc>-<pid>-dump " +
                " -XX:+Use<gc>"; //  -XX:MaxGCPauseMillis=1000 -XX:GCPauseIntervalMillis=3000

        // Сборщики мусора
        String[] GCs = {"G1GC", "ParallelGC", "SerialGC", "ConcMarkSweepGC"};
        // Профили тестирования
        String[] profiles = {
            // без утечки памяти с нарастанием "кэш"-объектов относительно кол-ва рабочих объектов (создаваемых и уничтожаемых)
            "10_000_000-100-0-0", "10_000_000-100-50-0", "10_000_000-100-100-0", "10_000_000-100-150-0", "10_000_000-100-500-0"/*,
            // с утечкой памяти с нарастанием "кэш"-объектов относительно кол-ва рабочих объектов (создаваемых и уничтожаемых)
            "10_000_000-100-0-20", "10_000_000-100-50-20", "10_000_000-100-100-20", "10_000_000-100-150-20", "10_000_000-100-500-20"*/
        };

        final File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdir();
        }

        for (String profile : profiles) {
            // Пересоздадим файлы для сохранения статистики в читабельном и csv-форматах
            final String fileName = "logs/statistics-" + profile;
            try (final BufferedWriter bwTxt = new BufferedWriter(new FileWriter(recreateFile(fileName + ".txt")))) {
                bwTxt.write(GcMeter.TXT_HEADER);
                bwTxt.newLine();
                bwTxt.flush();
            }

            for (String gc : GCs) {
                System.out.println("Test of gc \"" + gc + "\" with profile=\"" + profile + "\" has started.");
                Process p = Runtime.getRuntime().exec(javaPath + jvmOptions.replace("<gc>", gc) +
                    " -jar ./gcmeter.jar" +
                    " -gc=" + gc +
                    " -profile=" + profile);
                System.out.println("Test of gc \"" + gc + "\" with profile=\"" + profile + "\" has finished with code: " + p.waitFor() + "\n");
            }
        }
    }

    private static File recreateFile(final String fileName) throws IOException {
        final File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();

        return file;
    }

}
