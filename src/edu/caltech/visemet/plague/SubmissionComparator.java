package edu.caltech.visemet.plague;

import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public interface SubmissionComparator {

    double compare(Submission aSubmission, Submission anotherSubmission);

    double compare(List<Submission> submissions);
}
