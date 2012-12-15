package edu.caltech.visemet.plague;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

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
    public Submission(final int id, final File file) {
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

    @Override
    public int hashCode() {
        int hash = 5;

        hash = 83 * hash + this.id;
        hash = 83 * hash + Objects.hashCode(this.file);

        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }

        final Submission other = (Submission) obj;

        if (this.id != other.id) {
            return false;
        } else if (!Objects.equals(this.file, other.file)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Submission[");

        sb.append("id=").append(id);
        sb.append(", file=").append(file.getName());

        sb.append("]");

        return sb.toString();
    }
}
