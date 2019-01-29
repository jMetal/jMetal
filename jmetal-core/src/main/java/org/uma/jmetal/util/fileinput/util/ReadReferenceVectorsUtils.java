package org.uma.jmetal.util.fileinput.util;

import org.uma.jmetal.util.JMetalException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * created at 3:49 pm, 2019/1/29
 * the common util to read reference vectors/reference points/uniform weight vectors from file
 * @author sunhaoran
 */
public class ReadReferenceVectorsUtils {

    /**
     * @param filePath the file need to read
     * @return referenceVectors. referenceVectors[i][j] means the i-th vector's j-th value
     * @throws JMetalException if error while read file
     */
    public static double[][] readReferenceVectors(String filePath){
        double[][] referenceVectors ;
        try{
            String path = ReadReferenceVectorsUtils.class.getResource("/" + filePath).getPath() ;
            List<String> vectorStrList = Files.readAllLines(Paths.get(path)) ;
            referenceVectors = new double[vectorStrList.size()][] ;
            for(int i = 0;i < vectorStrList.size(); i++){
                String vectorStr = vectorStrList.get(i) ;
                String[] objectArray = vectorStr.split("\\s+") ;
                referenceVectors[i] = new double[objectArray.length] ;
                for(int j = 0; j < objectArray.length; j++){
                    referenceVectors[i][j] = Double.parseDouble(objectArray[j]) ;
                }
            }

        }catch (Exception e){
            throw new JMetalException("read file " + filePath + " error", e) ;
        }

        return referenceVectors ;
    }
}
