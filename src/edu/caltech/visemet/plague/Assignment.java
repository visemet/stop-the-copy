package edu.caltech.visemet.plague;

import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class Assignment {

    private final String filename;

    private final List<Submission> submissions = new ArrayList<>();

    private FilenameFilter filter = null;

    public Assignment(String filename) {
        this.filename = filename;
    }

    public void attach(Submission submission) {
        submissions.add(submission);
    }

    public Iterator<SubmissionPair> iterator() {
        return new AssignmentIterator(submissions);
    }

    public FilenameFilter asFilter() {
        if (filter == null) {
            filter = new AssignmentFilter(filename);
        }

        return filter;
    }

    public class SubmissionPair {

        private final Submission submission1;

        private final Submission submission2;

        public SubmissionPair(Submission submission1, Submission submission2) {
            this.submission1 = submission1;
            this.submission2 = submission2;
        }
    }

    private class AssignmentIterator implements Iterator<SubmissionPair> {

        private final List<Submission> submissions;

        private int index1 = 0;

        private int index2 = 1;

        public AssignmentIterator(List<Submission> submissions) {
            this.submissions = ImmutableList.copyOf(submissions);
        }

        @Override
        public boolean hasNext() {
            return index2 < submissions.size();
        }

        @Override
        public SubmissionPair next() {
            Submission submission1 = submissions.get(index1);
            Submission submission2 = submissions.get(index2);

            index2++;

            if (index2 == submissions.size()) {
                index1++;
                index2 = index1 + 1;
            }

            return new SubmissionPair(submission1, submission2);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }

    private class AssignmentFilter implements FilenameFilter {

        private final String filename;

        public AssignmentFilter(String filename) {
            this.filename = filename;
        }

        @Override
        public boolean accept(File dir, String name) {
                return name.equals(filename);
        }
    }
}
