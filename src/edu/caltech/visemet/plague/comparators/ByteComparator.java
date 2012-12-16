package edu.caltech.visemet.plague.comparators;

import edu.caltech.visemet.plague.CombinationIterator;
import edu.caltech.visemet.plague.Submission;
import edu.caltech.visemet.plague.SubmissionComparator;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class ByteComparator implements SubmissionComparator {

    @Override
    public double compare(Submission aSubmission, Submission anotherSubmission)
            throws FileNotFoundException, IOException {

        long matches = 0;
        long count = 0;

        BufferedInputStream aStream =
                new BufferedInputStream(aSubmission.asStream());

        BufferedInputStream anotherStream =
                new BufferedInputStream(anotherSubmission.asStream());

        while (true) {
            byte aByte = (byte) aStream.read();
            byte anotherByte = (byte) anotherStream.read();

            if (aByte == EOF || anotherByte == EOF) {
                break;
            }

            if (aByte == anotherByte) {
                matches++;
            }

            count++;
        }

        return ((double) matches / (double) count);
    }

    @Override
    public double compare(List<Submission> submissions, Aggregate aggregate)
            throws FileNotFoundException, IOException {

        Iterator<List<Submission>> iterator =
                new CombinationIterator<>(PAIR, submissions);

        double result = -1;

        double sum = result;
        int count = 0;

        double max = result;

        double min = result;

        switch (aggregate) {
        case AVERAGE:
            while (iterator.hasNext()) {
                List<Submission> next = iterator.next();

                Submission aSubmission = next.get(0);
                Submission anotherSubmission = next.get(1);

                double value = compare(aSubmission, anotherSubmission);

                sum += value;
                count++;
            }

            result = count == -1 ? result : (sum / count);
            break;
        case MAXIMUM:
            while (iterator.hasNext()) {
                List<Submission> next = iterator.next();

                Submission aSubmission = next.get(0);
                Submission anotherSubmission = next.get(1);

                double value = compare(aSubmission, anotherSubmission);

                if (max == -1 || value > max) {
                    max = value;
                }
            }

            result = max;
            break;
        case MINIMUM:
            while (iterator.hasNext()) {
                List<Submission> next = iterator.next();

                Submission aSubmission = next.get(0);
                Submission anotherSubmission = next.get(1);

                double value = compare(aSubmission, anotherSubmission);

                if (min == -1 || value < min) {
                    min = value;
                }
            }

            result = min;
            break;
        }

        return result;
    }
}
