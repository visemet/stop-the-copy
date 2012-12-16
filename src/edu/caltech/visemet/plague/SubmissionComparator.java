package edu.caltech.visemet.plague;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public interface SubmissionComparator {

    final byte EOF = (byte) -1;

    final int PAIR = 2;

    enum Aggregate {
        AVERAGE,
        MAXIMUM,
        MINIMUM;
    }

    double compare(Submission aSubmission, Submission anotherSubmission)
            throws FileNotFoundException, IOException;

    double compare(List<Submission> submissions, Aggregate aggregate)
            throws FileNotFoundException, IOException;
}
