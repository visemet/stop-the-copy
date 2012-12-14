package edu.caltech.visemet.plague;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class Submission {

    /**
     * Holds the identifier for this submission.
     */
    private final int id;

    /**
     * Holds the file of this submission.
     */
    private final File file;

    /**
     * Class constructor specifying an identifier and a file.
     *
     * @param id the identifier for this submission
     * @param file the file of this submission
     */
    public Submission(int id, File file) {
        this.id = id;
        this.file = file;
    }

    /**
     * Returns this submission represented as a file input stream.
     *
     * @return a file input stream of this submission
     *
     * @throws FileNotFoundException if the previously specified file does not
     * exist
     */
    public FileInputStream asStream() throws FileNotFoundException {
        return new FileInputStream(file);
    }
}
