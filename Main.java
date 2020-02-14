import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class Result {

    /*
     * Complete the 'PolynomialMult' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts following parameters:
     *  1. INTEGER n
     *  2. INTEGER_ARRAY p
     *  3. INTEGER_ARRAY q
     */

    // Multiply two polynomials
    public static List<Integer> PolynomialMult(int n, List<Integer> p, List<Integer> q) {
        //base case of when the polynomial coefficient is one digit
        if (n < 2){
            List<Integer> baseCList = new ArrayList<>(1);
            baseCList.add(Integer.valueOf(p.get(0).intValue() * q.get(0).intValue()));
            return baseCList;
        }
        
        //recursive case: the polynomial coefficient != one digit so you need to keep dividing into halves
        int div = n/2; //this indicates at which index the partition into two smaller parts will happen
        List<Integer> pHigh = p.subList(0,div); //represents an...a(n/2)
        List<Integer> pLow = p.subList(div,p.size()); //represents a(n/2 - 1)...a1
        List<Integer> qHigh = q.subList(0,div); // represents bn...b(n/2)
        List<Integer> qLow = q.subList(div,q.size()); //represents b(n/2 - 1)...b(1)
        
        //represents the "divide" part
        //recursively calling PolynomialMult into three of the smaller halves 
        //until it reaches of size 1.
        List<Integer> a = PolynomialMult(div, pHigh, qHigh); //recusive call on the higher part
        List<Integer> b = PolynomialMult(div, PolyAdd(pHigh,pLow), PolyAdd(qHigh,qLow));//recursive call on the high+low 
        List<Integer> c = PolynomialMult(div, pLow, qLow); //recursive call on the lower part
                       
        //represents the "conquer" part
        //return the merged polynomial coefficients from merging the three smaller halves 
        return Result.PolyMerge(n,a,b,c); 

    }
    
    private static List<Integer> PolyMerge(int degreeN, List<Integer> high, List<Integer> mid, List<Integer> low){
        //calculates the "(b-a-c)" list 
        List<Integer> midActual = PolySubtract(PolySubtract(mid, high),low);
        
        //create an empty list of size of three sublists merged with all of its elements as 0
        int totalNumCoeff = 2*degreeN - 1;
        List<Integer> list = new ArrayList<Integer>();
        for (int e = 0; e< totalNumCoeff; e ++){
            list.add(e,Integer.valueOf(0));
        }
        
        //calculates how many overlaps there are between two adjacent sublists
        //we are dividing by 2 because we know that we will always have three sublists 
        //and there will always be 2 overlaps
        int numOverlaps = (3*high.size() - totalNumCoeff) / 2;
        
        int indexFirstOverlap = high.size() - numOverlaps; //indicates the index where the first overlap happens
        int indexSecdOverlap = indexFirstOverlap + midActual.size() - numOverlaps; //indicates the index of 2nd overlap
        
        //if there is no overlap, meaning the sizes of sublists are 1
        if (numOverlaps == 0){
            list.set(0, Math.addExact(list.get(0), high.get(0)));
            list.set(1, Math.addExact(list.get(1), midActual.get(0)));
            list.set(2, Math.addExact(list.get(2), low.get(0)));
        }
        else{ //there exists some overlap, meaning the size of sublists are >1
            int counter1 = 0;
            int counter2 = 0;
            for (int i = 0; i < totalNumCoeff; i ++){
                if (i < high.size()){  
                    //add the 1st sublist that represents the higher part of the halved sequence of coefficients
                    list.set(i, Math.addExact(list.get(i), high.get(i)));
                 }
                if (i >= indexFirstOverlap && counter1!=midActual.size()){  
                    //add the 2nd sublist that represents the high+low part of the halved sequence of coefficients
                    list.set(i, Math.addExact(list.get(i), midActual.get(counter1)));
                    counter1++;
                }
                if (i >= indexSecdOverlap){
                    //add the 3rd sublist that represents the lower part of the halved sequence of coefficients
                    list.set(i, Math.addExact(list.get(i), low.get(counter2)));
                    counter2++;
                }          
            }
        }
        return list;
    }
    
    /*
     * Subtracts coefficients stored in one list, namely q, from coefficients stored in the other list, namely p
     * @return List<Integer> of the difference of the two sequences of coefficients
     */
    private static List<Integer> PolySubtract(List<Integer> p, List<Integer> q){
        List<Integer> polyMinused = new ArrayList<Integer>();
        for (int i = 0; i < p.size(); i ++){
            polyMinused.add(Math.subtractExact(p.get(i),q.get(i)));
        }
        return polyMinused;
    }
    
    /*
     * Adds coefficients stored in one list, namely q, from coefficients stored in the other list, namely p
     * @return List<Integer> of the sum of the two sequences of coefficients
     */
    private static List<Integer> PolyAdd(List<Integer> p, List<Integer> q){
        List<Integer> polyAdded = new ArrayList<Integer>();
        for (int i = 0; i < p.size(); i ++){
            polyAdded.add(Math.addExact(p.get(i),q.get(i)));
        }
        return polyAdded;
    }
    

}

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        List<Integer> p = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
            .map(Integer::parseInt)
            .collect(toList());

        List<Integer> q = Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
            .map(Integer::parseInt)
            .collect(toList());

        List<Integer> r = Result.PolynomialMult(n, p, q);

        bufferedWriter.write(
            r.stream()
                .map(Object::toString)
                .collect(joining(" "))
            + "\n"
        );

        bufferedReader.close();
        bufferedWriter.close();
    }
}
