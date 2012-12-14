package edu.caltech.visemet.plague;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Max Hirschhorn #visemet
 */
public class Setup {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // usage: directory file1 file2 ... filen

        String directory;
        List<String> filenames = new ArrayList<>();

        // @todo: do actual argument parsing...

        directory = args[0];

        for (int index = 1; index < args.length; index++) {
            String arg = args[index];

            filenames.add(arg);
        }

        DirectoryScanner scanner = new DirectoryScanner(directory, filenames);
        List<Assignment> assignments = scanner.prepare();
    }
}
