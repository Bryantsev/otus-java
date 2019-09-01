package ru.otus.lesson9;

import com.google.common.base.Strings;
import com.sun.management.GarbageCollectionNotificationInfo;
import com.sun.management.GcInfo;

import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.openmbean.CompositeData;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.lang.management.GarbageCollectorMXBean;
import java.util.*;

/**
 * Created by Alexander Bryantsev on 07.08.2019.
 */
public class GcMeter {

    final static String TXT_HEADER =
        Strings.padEnd("GcName", 17, ' ') + " || " +
            Strings.padEnd("GcNameDetail", 23, ' ') + " || " +
            "processedObjects || " +
            "gcRunCount || " +
            "gcDuration, ms || " +
            "durationAllWork, ms || " +
            "gcDuration/durationAllWork, % || " +
            "gcDuration/sec, ms/sec";
    final static String LINE_STR = "-".repeat(TXT_HEADER.length());

    /**
     * @param args Параметры:
     *             -gc=<Имя Gc>
     *             -profile=<кол-во объектов всего>-<кол-во циклов>-<% "старых"/постоянных объектов - аналог кэша>-<% утечки памяти от размера массива на цикл>
     */
    public static void main(String[] args) throws Exception {

        Map<String, GcStat> gcStat = new HashMap<>();

        // System.out.println("Starting pid: " + ManagementFactory.getRuntimeMXBean().getName());
        switchOnMonitoring(gcStat);

        int size = 10_000_000;
        int loopCounter = 10;
        int oldPercent = 0;
        int leakMemoryPercent = 0;


        final Properties properties = new Properties();
        properties.load(new StringReader(Arrays.toString(args).
            replace(", ", "\n").
            replace("[", "").
            replace("]", "")));
        // System.out.println(properties);
        String gcName = properties.getProperty("-gc", "Unknown Gc");
        String profile = properties.getProperty("-profile", "Unknown");
        String[] profileData = profile.split("-");
        if (!"Unknown".equals(profile) && profileData.length == 4) {
            size = Integer.valueOf(profileData[0].replace("_", ""));
            loopCounter = Integer.valueOf(profileData[1]);
            oldPercent = Integer.valueOf(profileData[2]);
            leakMemoryPercent = Integer.valueOf(profileData[3]);
        }

        //MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        //ObjectName name = new ObjectName("ru.otus:type=Benchmark");

        Benchmark benchmark = new Benchmark(size, loopCounter, oldPercent, leakMemoryPercent);
        //mbs.registerMBean(benchmark, name);
        //benchmark.setSize(size);

        long beginTime = System.currentTimeMillis();
        long processedObjects = benchmark.run();
        final long durationAllWork = System.currentTimeMillis() - beginTime;
        System.out.println("durationAllWork: " + (double) durationAllWork / 1_000 + " sec");
        calcAndSaveStatistics(gcName, profile, durationAllWork, gcStat, processedObjects);
    }

    private static void calcAndSaveStatistics(String gcName, String profile, long durationAllWork, Map<String, GcStat> gcStat, long processedObjects) throws IOException {
        // Рассчитаем собранную статистику по GC
        System.out.println(TXT_HEADER);
        System.out.println(LINE_STR);
        final String fileName = "logs/statistics-" + profile;
        try (final BufferedWriter bwTxt = new BufferedWriter(new FileWriter(fileName + ".txt", true));
             // final BufferedWriter bwCsv = new BufferedWriter(new FileWriter(fileName + ".csv", true))
        ) {
            bwTxt.write(LINE_STR);
            bwTxt.newLine();

            long gcDurationAll = 0;
            int gcRunCountAll = 0;
            for (Map.Entry<String, GcStat> statEntry : gcStat.entrySet()) {

                long gcDuration = 0;
                int gcRunCount = 0;
                for (List<Long> durationList : statEntry.getValue().getActionData().values()) {
                    gcRunCount += durationList.size();
                    for (Long duration : durationList) {
                        gcDuration = gcDuration + duration;
                    }
                }

                gcDurationAll += gcDuration;
                gcRunCountAll += gcRunCount;

                final String statisticRow = getStatisticString(gcName, statEntry.getKey(), processedObjects, durationAllWork, gcRunCount, gcDuration);
                System.out.println(statisticRow);
                bwTxt.write(statisticRow);
                bwTxt.newLine();
            }

            final String statisticRow = getStatisticString(gcName, "All", processedObjects, durationAllWork, gcRunCountAll, gcDurationAll);
            System.out.println(statisticRow);
            bwTxt.write(statisticRow);
            bwTxt.newLine();

            bwTxt.flush();
            // bwCsv.flush();
        }
    }

    private static String getStatisticString(String gcName, String gcNameDetail, long processedObjects, long durationAllWork, int gcRunCount, long gcDuration) {
        return Strings.padEnd(gcName, 17, ' ') + " || " +
            Strings.padEnd(gcNameDetail, 23, ' ') + " || " +
            Strings.padStart("" + processedObjects, 16, ' ') + " || " +
            Strings.padStart("" + gcRunCount, 10, ' ') + " || " +
            Strings.padStart("" + gcDuration, 14, ' ') + " || " +
            Strings.padStart("" + durationAllWork, 19, ' ') + " || " +
            Strings.padStart("" + gcDuration * 100 / durationAllWork, 29, ' ') + " || " +
            Strings.padStart("" + gcDuration * 1_000 / durationAllWork, 22, ' ');
    }

    private static void switchOnMonitoring(Map<String, GcStat> gcStat) {
        List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        NotificationListener listener = (notification, handback) -> {
            if (notification.getType().equals(GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION)) {
                GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());

                GcInfo gcInfo = info.getGcInfo();

                gcStat.get(info.getGcName()).addAction(info.getGcAction(), gcInfo.getDuration());
            }
        };

        for (GarbageCollectorMXBean gcbean : gcbeans) {
            System.out.println("GC name: " + gcbean.getName());
            gcStat.put(gcbean.getName(), new GcStat());
            ((NotificationEmitter) gcbean).addNotificationListener(listener, null, null);
        }
    }

}
