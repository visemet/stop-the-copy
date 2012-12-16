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

    double compare(Submission aSubmission, Submission anotherSubmission)
            throws FileNotFoundException, IOException;

    double compare(List<Submission> submissions)
            throws FileNotFoundException, IOException;
}
