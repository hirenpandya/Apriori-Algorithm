import java.util.*;
import java.io.*;

/**
 * @author hirenp
 * Apriori implementation 
 * 
 */

public class Apriori {
    public static HashMap <String,String> permutations = new LinkedHashMap<>();

    public static void main(String[] args)throws IOException  {
        
        Scanner scan = new Scanner(System.in);
	// Java method to get current working directory 
        String curr_dir = System.getProperty("user.dir");
        
        // Asking User for Path
        System.out.println("Please enter the path of data file: ");
        String path = scan.nextLine();
        
	// temporary results like all permutations are stored in below files
        File file1 =new File(curr_dir+"/temp1");
        file1.delete();
        File file2 =new File(curr_dir+"/frequent_item_set");
        file2.delete();

	// Final output file which contains all the rules
        File file3 =new File(curr_dir+"/Rules");
        file3.delete();
        
        // Asking User for Minimum support which should be between 0 and 1
        System.out.println("Enter Minimum Support rate(IN range of 0.0 - 1.0)");
        float min_support = scan.nextFloat();
        if(min_support <0 || min_support > 1){
            System.out.println("Invalid Support value. Its value can be between 0 and 1");
            System.exit(0);
        }
        
        // Asking User for Minimum Confidence
        System.out.println("Enter Minimum confidence rate(IN range of 0.0 - 1.0)");
        float min_confidence = scan.nextFloat();
        if(min_confidence<0  || min_confidence >1){
            System.out.println("Invalid confidence value. Its value can be between 0 and 1");
            System.exit(0);
        }

        String e_path = curr_dir+"/frequent_item_set";

        try {
	    // One item frequent itemset
            HashMap <String,Integer> first_frequent_itemset = new LinkedHashMap<String,Integer>();
	    // Two item frequent itemset
            HashMap <String,Integer> second_frequent_itemset = new LinkedHashMap<String,Integer>();
	    // Frequent item set which contains all valid item set
            HashMap <String,Integer> frequent_itemset = new LinkedHashMap<String,Integer>();
	    // Frequent item set which contains all valid itemset with all possible permutation-combinations 
            HashMap <String,Integer> frequent_itemset_final = new LinkedHashMap<String,Integer>();

	    // One item candidate set
            ArrayList<String>  candidate_set1 = new ArrayList<String>();
	    // Two item candidate set
            ArrayList <String> candidate_set2 = new ArrayList<String>(); 

            FileReader input_file = new FileReader(path);
            BufferedReader br_input_file = new BufferedReader(input_file);
            String line_input_file;
            int lncnt = -2;
	    // Counting number of rows from input file 
            while((line_input_file = br_input_file.readLine())!=null){
                lncnt = lncnt + 1;
            }            
            
            int my_count = 0;
	    // header1 is a hashmap which contains all headers with their respective name 
            HashMap <Integer, String> header1 = new LinkedHashMap<Integer, String>();
            input_file= new FileReader(path);
            br_input_file = new BufferedReader(input_file);

            String current_line;

            while((current_line = br_input_file.readLine())!=null){
                String[] tokens = current_line.split("\\s+");
                if(my_count==0){
                    for (int i = 0; i < tokens.length; i++) {
			// First line of the row is a header
                        header1.put(i, tokens[i]);
                    }
                } else{
                    for (int i = 0; i < tokens.length; i++) {
                        String temp = header1.get(i)+"="+tokens[i];
                        candidate_set1.add(temp);
                    }                    
                }
                my_count++;            
            }            
            
            System.out.println("Candidate Set 1: ");
            System.out.println(candidate_set1);       
            
            // get_frequent_itemset() method makes the ith Frequent item itemset from (i-1)th candidate item set
            // Parameters are : (i-1)th candidate item set, input data file, minimum support provided by useer, i iteration, number of total rows) 
            first_frequent_itemset = get_frequent_itemset(candidate_set1, path, min_support,1, lncnt);
            System.out.println("\nFrequent Set 1: ");
            System.out.println(first_frequent_itemset);

            // Generting First frequent item set 
            Set set1 = first_frequent_itemset.entrySet();
            Iterator itq1 = set1.iterator();
            while(itq1.hasNext()) {
                Map.Entry mentry = (Map.Entry)itq1.next();
                String k = (String) mentry.getKey();
                int v = (int) mentry.getValue();
                k.trim();
                frequent_itemset.put(k, v);
            }

             // candidate_itemset method makes ith candidate item set. In this case, it would be 2
             // Two parameters :  previous frequent itemset, i
            candidate_set2 = candidate_itemset(first_frequent_itemset,2);   
            System.out.println("\nCandidate Set 2: ");
            System.out.println(candidate_set2);
            
            // get_frequent_itemset() method makes the ith Frequent item itemset from (i-1)th candidate item set
            // Parameters are : (i-1)th candidate item set, input data file, minimum support provided by user, i iteration, number of total rows) 
            second_frequent_itemset = get_frequent_itemset(candidate_set2, path, min_support, 2, lncnt);
            System.out.println("\nFrequent Set 2: ");
            System.out.println(second_frequent_itemset);

            Set set4 = second_frequent_itemset.entrySet();
            Iterator itq2 = set4.iterator();
            while(itq2.hasNext()) {
                Map.Entry mentry = (Map.Entry)itq2.next();
                String k = (String) mentry.getKey();
                int v = (int) mentry.getValue();
                k.trim();
                frequent_itemset.put(k, v);
            }

            // Temporary results are stored in file
            write_to_outputfile(second_frequent_itemset,2,e_path);
            HashMap <String,Integer> general_frequent_itemset = new LinkedHashMap<String,Integer>();
            ArrayList<String> general_candidate_set           = new ArrayList<String>();
            int count=3; 

            // Below while loop generates K-Frequent item set 
            while(!second_frequent_itemset.isEmpty()){             
                general_candidate_set = get_candidate_set(second_frequent_itemset,(count-1));
                System.out.println("\nCandidate Set "+ count+" :");
                System.out.println(general_candidate_set);                
                second_frequent_itemset.clear();
                general_frequent_itemset = get_frequent_itemset(general_candidate_set, path, min_support,count, lncnt);
                System.out.println("\nFrequent set "+count+":");
                System.out.println(general_frequent_itemset);

                Set q4 = general_frequent_itemset.entrySet();
                Iterator itq4 = q4.iterator();
                while(itq4.hasNext()) {
                    Map.Entry mentry = (Map.Entry)itq4.next();
                    String k = (String) mentry.getKey();
                    int v = (int) mentry.getValue();
                    k.trim();
                    k.replaceAll("\\s+$", "");
                    frequent_itemset.put(k, v);
                }
                
                // Temporary results are stored in file
                write_to_outputfile(general_frequent_itemset,count,e_path);
                Set set2 = general_frequent_itemset.entrySet();
                Iterator iterator2 = set2.iterator();
                while(iterator2.hasNext()) {
                    Map.Entry mentry = (Map.Entry)iterator2.next();
                    String hash_key = (String) mentry.getKey();
                    int hash_value = (int) mentry.getValue();
                    second_frequent_itemset.put(hash_key, hash_value);
                }
                general_frequent_itemset.clear();
                count++;
            }
            
	    // Below while loop generates a hashmap which contains the frequent itemset with all permutation-combination
            Set se2 = frequent_itemset.entrySet();
            Iterator ite2 = se2.iterator();
            while(ite2.hasNext()) {
                Map.Entry mentry = (Map.Entry)ite2.next();
                String q_key     = (String) mentry.getKey();
                int q_val        = (Integer) mentry.getValue();
                
                HashMap <Integer,String> word_to_number = new LinkedHashMap<Integer,String>();
                String[] input = q_key.trim().split("\\s+");
                if(input.length>=2){
                    for(int i=0;i<input.length;i++){
		        // Converting word to number
                        word_to_number.put(i, input[i]);
                    }

                    String permu_input="";
                    Set wtn = word_to_number.entrySet();
                    Iterator it9 = wtn.iterator();
                    while(it9.hasNext()) {
                       Map.Entry men8 = (Map.Entry)it9.next();
                       permu_input = permu_input+ men8.getKey();

                    }

                    HashMap <String,String> permutation_hash = new LinkedHashMap<>();
		    // Applying permutation
                    permutation_hash = get_permutation(permu_input);                    
                    
                    Set tq8 = permutation_hash.entrySet();
                    Iterator itq8 = tq8.iterator();
                    while(itq8.hasNext()) {
                        Map.Entry menn48 = (Map.Entry)itq8.next();
                        String ln = (String) menn48.getKey();

                        String[] d = ln.split("");

                        String final_key=""; 
                        int f=0;
                        for(String se : d){
                            int j = Integer.parseInt(se);
                            String b = word_to_number.get(j);
                            if(f==0){
                                final_key = b;
                            } else{
                                final_key = final_key+" "+b;	
                            }

                            f++;
                        }   

                        String ln2 = (String) menn48.getValue();
                        String[] d2 = ln2.split("");

                        String final_val="";
                        int t=0;
                        for(String se : d2){
                            int j = Integer.parseInt(se);
                            String b = word_to_number.get(j);

                            if(t==0){
                                final_val = b;
                            } else{
                                final_val = final_val+" "+b;	
                            }

                            t++;
                        }
                        String out_per = final_key + " " + final_val;
                        
                        frequent_itemset_final.put(out_per, q_val);
                    }

                    permutation_hash.clear();
                    word_to_number.clear();
                } else {
                    frequent_itemset_final.put(q_key, q_val);
                }
            }

            File f3 =new File(curr_dir+"/temp1");
            f3.delete();
            if(!f3.exists()){
                f3.createNewFile();
            }
            
            FileWriter fw2 = new FileWriter(f3.getAbsoluteFile(),true);
            BufferedWriter bw2 = new BufferedWriter(fw2);

            // This file contains all frequent itemset with all permutation-combination
            File frequent_item_set = new File(curr_dir+"/frequent_item_set");
            
            FileReader ff= new FileReader(frequent_item_set.getAbsolutePath());
            BufferedReader bb = new BufferedReader(ff);
            HashMap <String, String> hr = new LinkedHashMap<String, String>();

            int v = 1;
            String line;

            while((line = bb.readLine())!=null){
                //System.out.println(line);
                if(line.contains("Itemset")){

                } else {
                    int length = line.split("\\s+").length;
                    if(length>=2){
                        HashMap <Integer,String> h = new LinkedHashMap<Integer,String>();
                        String left[] = line.split("####");
                        String[] input = left[0].trim().split("\\s+");

                        //this for loop will convert word to int and and this integer is as a input to permutation function 
                        for(int i=0;i<input.length;i++){
                            h.put(i, input[i]);
                        }
                        
                        String permu_input="";
                        Set wtn = h.entrySet();
                        Iterator it9 = wtn.iterator();
                        while(it9.hasNext()) {
                           Map.Entry mentry = (Map.Entry)it9.next();
                           permu_input = permu_input+ mentry.getKey();
                        }
                                                
                        HashMap <String,String> v2 = new LinkedHashMap<>();

                        v2 = get_rules(permu_input);                       
                        Set tq8 = v2.entrySet();
                        Iterator itq8 = tq8.iterator();
                        while(itq8.hasNext()) {
                            Map.Entry mentry = (Map.Entry)itq8.next();
                            String p_line = (String) mentry.getKey();
                            String[] d = p_line.split("");
                            String final_key = "";
                            int f = 0;
                            for(String se : d){
                                int j = Integer.parseInt(se);
                                String b = h.get(j);
                                if(f==0){
                                    final_key = b;
                                } else{
                                    final_key = final_key+" "+b;
                                }
                                f++;
                            }   

                            String ln2 = (String) mentry.getValue();
                            String[] d2 = ln2.split("");
                            String final_val="";
                            int t=0;
                            for(String se : d2){
                                int j = Integer.parseInt(se);

                                String b = h.get(j);
                                if(t==0){
                                    final_val = b;
                                } else{
                                    final_val = final_val+" "+b;	
                                }

                                t++;
                            }
                            String out_per = final_key + "&&&&" + final_val + "\n";
                            bw2.write(out_per);
                        }
                        
                        v++;       
                        h.clear();
                    
                    }
                                              
                }
            }
            bw2.close();

            //this is file where all rules will be stored.
            File fout = new File(curr_dir+"/Rules");
            if(!fout.exists()){
                fout.createNewFile();
            }
            FileWriter ffout= new FileWriter(fout.getAbsolutePath(),true);
            BufferedWriter bbout = new BufferedWriter(ffout);
            bbout.write("Summary:"+"\n");
            bbout.write("Total number of rows in original set : "+lncnt+"\n");
            bbout.write("The selected Measures : Support="+min_support+" ," + "Confidence="+ min_confidence+"\n");
            bbout.write("------------------------------------------"+"\n");
            bbout.write("Discovered Rules:" + "\n\n");
            
            FileReader fw3 = new FileReader(f3.getAbsolutePath());
            BufferedReader bw3;
            bw3 = new BufferedReader(fw3);
            System.out.println("\n The Rule Generation : \n");
            String cline = "";
            int rule_no = 1;
            while ((cline = bw3.readLine()) != null) {
                String[] key_val = cline.split("&&&&");
                String qd = key_val[0]+" "+key_val[1];
                //System.out.println(frequent_itemset_final.get(key_val[0]) + " --> " + lncnt);
                float support = (float) frequent_itemset_final.get(qd)/lncnt;
                float supp_b = (float) frequent_itemset_final.get(key_val[0])/(float)lncnt;
                float confidence =  support/supp_b;
		if(confidence>=min_confidence){
		        String final_rule = "Rule no. "+rule_no+": (Support="+support+", Confidence="+confidence+") \n" + "{ "+key_val[0]+"}\n" + "----> {"+key_val[1]+"} ";
		        System.out.println(final_rule + "\n\n");
		        bbout.write(final_rule + "\n\n");
		        rule_no++;
		}
            }

            bbout.write("\nTotal rules discovered:"+ (rule_no-1)+"\n");
            bbout.close();

            System.out.println("\n\n ");
            System.out.println("----Algorithm Finished----");

        } catch (Exception e) {
            System.out.println("Error");
        }
        
        File f1 =new File(curr_dir+"/temp1");
        f1.delete();
        File f2 =new File(curr_dir+"/frequent_item_set");
        f2.delete();        
        
    }
    
    //For applying permutation-combination
    public static HashMap<String, String> get_permutation(String str) throws IOException { 
        HashMap <String,String> v1 = new LinkedHashMap<>();
        HashMap <String,String> v2 = new LinkedHashMap<>();
        get_permutation("", str,v1); 

        Set tq = v1.entrySet();
        Iterator itq2 = tq.iterator();
        while(itq2.hasNext()) {
            Map.Entry mentry = (Map.Entry)itq2.next();
            String hash_key = (String) mentry.getKey();
            String hash_val = (String) mentry.getValue();
            if(hash_key.length() == (str.length()-1)){
                v2.put(hash_key, hash_val);
            }
        }        

        return v2;
    }

    public static void get_permutation(String prefix, String str, HashMap<String, String> v1) throws IOException {
        int n = str.length();
        if (n == 0){

        }
        else {
            for (int i = 0; i < n; i++){
                char[] chars = prefix.toCharArray();
                String sorted = new String(chars);
                String c = str.charAt(i)+"";
                if(!sorted.equals("")){
                    v1.put(sorted, c);
                }
                get_permutation(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),v1);
            }
        }
    }

    //For applying permutation-combination
    public static HashMap<String, String> get_rules(String str) throws IOException { 
        HashMap <String,String> v1 = new LinkedHashMap<>();

        get_rules("", str,v1); 
        Set tq = v1.entrySet();
        Iterator itq2 = tq.iterator();
        while(itq2.hasNext()) {
            String[] str_words = str.split("");
            ArrayList<String> list1 = new ArrayList<String>();

            for(String strr : str_words){
                list1.add(strr);
            }            
            Map.Entry mentry = (Map.Entry)itq2.next();
            String hash_key = (String) mentry.getKey();
            String[] key_vals = hash_key.split("");
            ArrayList<String> list2 = new ArrayList<String>();

            for(String str2 : key_vals){
                list2.add(str2);
            }  
            
            list1.removeAll(list2);
            String vall = "";
            for(int i=0; i<list1.size(); i++){
                vall = vall + list1.get(i);
            }
 
            v1.put(hash_key, vall);
        }
        
        return v1;
    }

    public static void get_rules(String prefix, String str, HashMap<String, String> v1) throws IOException {

        int n = str.length();
        if (n == 0){

        }
        else {
            for (int i = 0; i < n; i++){
                char[] chars = prefix.toCharArray();
                Arrays.sort(chars);
                String sorted = new String(chars);
                String c = str.charAt(i)+"";
                if(!sorted.equals("")){
                    v1.put(sorted, c);
                }
                get_rules(prefix + str.charAt(i), str.substring(0, i) + str.substring(i+1, n),v1);
            }
        }
    }

    
    
    // Writing final output to output file 
    public static void write_to_outputfile(HashMap<String, Integer> m, int i,String output_path) {
        try {
            File file = new File(output_path);
            if (!file.exists()) {	// create a file if it doesn't exist
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write("\nItemset "+ i + "\n");

            Set set88 = m.entrySet();
            Iterator iterator88 = set88.iterator();
            while(iterator88.hasNext()) {
                Map.Entry mentry88 = (Map.Entry)iterator88.next();
                String hash_key = (String) mentry88.getKey();
                int hash_value = (int) mentry88.getValue();
                bw.write(hash_key+" #### "+hash_value+"\n");
            }
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }		
    }

    //For performing Intersection between two arrays
    private static ArrayList<String> intersection(String[] f1, String[] f2) {
        ArrayList <String> common_arr = new ArrayList<String>();
        for (String d1:f1) {
            for (String d2:f2) {
                if ((d1.equals(d2))) {
                    common_arr.add(d1);
                }
            }
        }

        return common_arr;
    }    
    
    // To make candidate itemset from  previous frequent item set
    // Parameters : Hashmap with previous frequent item set and itemsetNumber
    private static ArrayList<String> get_candidate_set(HashMap<String, Integer> map1, int curr_iteration) {
        ArrayList <String> arr = new ArrayList<String>(); 
        ArrayList<String> arr2 = new ArrayList<String>();

        Set set = map1.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
           Map.Entry mentry = (Map.Entry)iterator.next();
           arr.add((String) mentry.getKey());           
        }

        int len = arr.size();

        for(int k=0; k<(len-1); k++){
            for(int l=k+1; l<len; l++){
                ArrayList <String> a1 = new ArrayList<String>();

                ArrayList <String> common = new ArrayList<String>();
                ArrayList <String> f1 = new ArrayList<String>();
                ArrayList <String> f2 = new ArrayList<String>();

                String[] first = arr.get(k).split("\\s+");
                String[] second = arr.get(l).split("\\s+");

                for(String g:first){
                    f1.add(g);
                }

                for(String w:second){
                    f2.add(w);
                }
                String flag = "";
                for(int b=0; b<(curr_iteration-1); b++){
                    if(first[b].equals(second[b])){
                        flag = "true";
                    } else {
                        flag = "false";
                        break;
                    }
                }

                int first_length = f1.size();
                if(flag.equals("true")){
                    common = intersection(first,second);

                    if(common.size() == (first_length-1)){
                        a1.addAll(f1);
                        a1.removeAll(f2);
                        a1.addAll(f2); 

                        String final_string="";
                        for(String h:a1){
                            final_string = final_string+h+" ";
                        }
                        arr2.add(final_string);
                    } 
                }
            }
        }

       return arr2;
    }

    // To make frequent item set based on candidate item set
    // It also does "bonus" part "pruning"
    public static HashMap<String, Integer> get_frequent_itemset(ArrayList<String> a, String path, float min_supp, int curr_iteration, int tr) throws IOException {
        HashMap<String, Integer> map1 = new HashMap<String, Integer>();
        HashMap<Integer, String> map2 = new HashMap<Integer, String>();

        for(String word : a){
            int arr_cnt = 0;
            String current_line;
            BufferedReader br = new BufferedReader(new FileReader(path));
            int line_count = 0;
            while ((current_line = br.readLine()) != null) {
                String[] words = word.split("\\s+");
                if(line_count==0){
                    String[] curr_words= current_line.split("\\s+");
                    for (int i = 0; i < curr_words.length; i++) {
                        map2.put(i, curr_words[i]);
                    }  
                } else{           
                    int compare_num=0;
                    for(int j=0; j<curr_iteration; j++){
                        String[] line_words = current_line.split("\\s+");
                        int line_cnt = 0;
                        for(String line_wd : line_words){
                            String temp2 = map2.get(line_cnt)+"="+line_wd;
                            if(temp2.equals(words[j])){
                                compare_num++;
                            }
                            line_cnt++;
                        }
                    }

                    if(compare_num == curr_iteration){
                        arr_cnt++;
                        float count = (float) arr_cnt/tr;

			// Below If condition performs "bonus" part which is pruning
                        // Pruning - filter out those candidates which don't satisfy minimum support
                        if(count>=min_supp){
                            map1.put(word, arr_cnt);
                        }
                    }
                }
                line_count++;
            }
        }
        return map1;
    }
    
    //To make 2 Candidate itemset
    private static ArrayList<String> candidate_itemset(HashMap<String, Integer> map1, int itemsetNumber) {
        ArrayList <String> arr2 = new ArrayList<String>();
        ArrayList <String> arr1 = new ArrayList<String>();

        for (String key : map1.keySet()) {
            String result = key.trim();
            arr2.add(result);
        }
   
        int length = arr2.size();
       
        for(int i=0; i<length; i++){
            for(int j=i+1; j<length; j++){
                arr1.add(arr2.get(i)+ " "+arr2.get(j));
            }
        }

        return arr1;
    }
 
}
