package adventguardsleep;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Guards {

    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Paths.get(Guards.class.getResource("problem-input.txt").toURI()));
        SortedSet<String> objects = new TreeSet<>(input);

        Map<Integer, SortedSet<String>> stateByGuard = new HashMap<>();
        int actualGuard = 0;

        for (String string : objects) {
            if (string.contains("shift")) {
                actualGuard = Integer.valueOf(string.substring(string.indexOf('#') + 1, string.indexOf(" begins")));
                if (stateByGuard.containsKey(actualGuard)) {
                    stateByGuard.get(actualGuard).add(string);
                } else {
                    SortedSet<String> strings = new TreeSet<>();
                    strings.add(string);
                    stateByGuard.put(actualGuard, strings);
                }
            } else {
                stateByGuard.get(actualGuard).add(string);
            }
        }

        Map<Integer, Map<LocalTime, Integer>> frequencyOfSleepTimeByGard = new HashMap<>();

        for (Map.Entry<Integer, SortedSet<String>> integerSortedSetEntry : stateByGuard.entrySet()) {
            frequencyOfSleepTimeByGard.put(integerSortedSetEntry.getKey(), new HashMap<>());
            LocalTime sleepBegin = LocalTime.of(0, 0);
            for (String s : integerSortedSetEntry.getValue()) {
                if (s.contains("asleep")) {
                    sleepBegin = LocalTime.parse(s.substring(s.indexOf(' ') + 1, s.indexOf("] ")));
                } else if (s.contains("wakes")) {
                    LocalTime wakeUp = LocalTime.parse(s.substring(s.indexOf(' ') + 1, s.indexOf("] ")));

                    while (!sleepBegin.equals(wakeUp)) {
                        Integer time = frequencyOfSleepTimeByGard.get(integerSortedSetEntry.getKey()).putIfAbsent(sleepBegin, 1);
                        if (time != null) {
                            frequencyOfSleepTimeByGard.get(integerSortedSetEntry.getKey()).put(sleepBegin, frequencyOfSleepTimeByGard.get(integerSortedSetEntry.getKey()).get(sleepBegin) + 1);
                        }
                        sleepBegin = sleepBegin.plus(1, ChronoUnit.MINUTES);
                    }
                    sleepBegin = LocalTime.of(0, 0);
                }
            }

        }

        int guardWithBiggestSleepTime = 0;
        int sleepTime = 0;
        LocalTime minuteWhenSleepingMost = LocalTime.of(0, 0);

        // Guard sleeping the more
        for (Map.Entry<Integer, Map<LocalTime, Integer>> integerMapEntry : frequencyOfSleepTimeByGard.entrySet()) {

            LocalTime minuteWhenSleepingMostForThisGuard = LocalTime.of(0, 0);
            int sleepingTime = 0;
            int moreFrequenciesOfSleep = 0;
            for (Map.Entry<LocalTime, Integer> localTimeIntegerEntry : integerMapEntry.getValue().entrySet()) {
                sleepingTime += localTimeIntegerEntry.getValue();
                if (localTimeIntegerEntry.getValue() > moreFrequenciesOfSleep) {
                    moreFrequenciesOfSleep = localTimeIntegerEntry.getValue();
                    minuteWhenSleepingMostForThisGuard = localTimeIntegerEntry.getKey();
                }
            }

            if (sleepingTime > sleepTime) {
                guardWithBiggestSleepTime = integerMapEntry.getKey();
                sleepTime = sleepingTime;
                minuteWhenSleepingMost = minuteWhenSleepingMostForThisGuard;
            }
        }

        int result = guardWithBiggestSleepTime * minuteWhenSleepingMost.getMinute();

        System.out.println("Answer part 1: " + result);

        //Guard sleeping the more frequently at wich minute
        int guardId = 0;
        int moreFrequenciesOfSleep = 0;
        LocalTime minuteWhereSleepingTheMore = LocalTime.of(0, 0);

        for (Map.Entry<Integer, Map<LocalTime, Integer>> integerMapEntry : frequencyOfSleepTimeByGard.entrySet()) {

            LocalTime minuteWhenSleepingMostForThisGuard = LocalTime.of(0, 0);
            for (Map.Entry<LocalTime, Integer> localTimeIntegerEntry : integerMapEntry.getValue().entrySet()) {
                if (localTimeIntegerEntry.getValue() > moreFrequenciesOfSleep) {
                    guardId = integerMapEntry.getKey();
                    moreFrequenciesOfSleep = localTimeIntegerEntry.getValue();
                    minuteWhereSleepingTheMore = localTimeIntegerEntry.getKey();
                }
            }
        }

        System.out.println("Answer part 2: " + guardId * minuteWhereSleepingTheMore.getMinute());
    }

}
