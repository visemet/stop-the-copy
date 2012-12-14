package edu.caltech.visemet.plague;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class DirectoryScanner {

    private final String directory;

    private final List<String> filenames;

    public DirectoryScanner(String directory, List<String> filenames) {
        this.directory = directory;
        this.filenames = filenames;
    }

    public List<Assignment> prepare() {
        List<Assignment> assignments = new ArrayList<>();

        for (String filename : filenames) {
            Assignment assignment = new Assignment(filename);
            assignments.add(assignment);
        }

        File path = new File(directory);

        // @todo: check path actually is directory

        File[] subpaths = path.listFiles();

        for (int index = 0; index < subpaths.length; index++) {
            File subpath = subpaths[index];

            // @todo: check subpath actually is directory

            for (Assignment assignment : assignments) {
                File[] files = subpath.listFiles(assignment.asFilter());

                // @todo: check files contains at most a single file

                File file = files[0];
                Submission submission = new Submission(index, file);

                assignment.attach(submission);
            }
        }

        return assignments;
    }
}
