package edu.caltech.visemet.plague;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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

    public FilenameFilter asFilter() {
        if (filter == null) {
            filter = new AssignmentFilter(filename);
        }

        return filter;
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
