package edu.caltech.visemet.plague.comparators;

import edu.caltech.visemet.plague.Submission;
import edu.caltech.visemet.plague.SubmissionComparator;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public double compare(List<Submission> submissions)
            throws FileNotFoundException, IOException {

        throw new UnsupportedOperationException("Not supported yet.");
    }

}
