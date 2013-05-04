/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package myPKG;

/*
 * Ajitsen: Sample program for ChainMapper/ChainReducer. This program is modified version of WordCount example available in Hadoop-0.18.0. Added ChainMapper/ChainReducer and made to works in Hadoop 1.0.2.
 */

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapred.lib.ChainReducer;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class ChainWordCount extends Configured implements Tool {

    public static class Tokenizer extends MapReduceBase
    implements Mapper {

        private final static IntWritable one = new IntWritable(1);
        private Text word = new Text();

        public void map(LongWritable key, Text value,
                OutputCollector output,
                Reporter reporter) throws IOException {
            String line = value.toString();
            System.out.println("Line:"+line);
            StringTokenizer itr = new StringTokenizer(line);
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                output.collect(word, one);
            }
        }
    }

    public static class LowerCaser extends MapReduceBase
    implements Mapper {

        public void map(Text key, IntWritable value,
                OutputCollector output,
                Reporter reporter) throws IOException {
            String word = key.toString().toLowerCase();
            System.out.println("Lower Case:"+word);
            output.collect(new Text(word), value);
        }
    }

    public static class Reduce extends MapReduceBase
    implements Reducer {

        public void reduce(Text key, Iterator values,
                OutputCollector output,
                Reporter reporter) throws IOException {
            int sum = 0;
            while (values.hasNext()) {
                sum += values.next().get();
            }
            System.out.println("Word:"+key.toString()+"\tCount:"+sum);
            output.collect(key, new IntWritable(sum));
        }
    }

    static int printUsage() {
        System.out.println("wordcount  ");
        ToolRunner.printGenericCommandUsage(System.out);
        return -1;
    }

    public int run(String[] args) throws Exception {
        JobConf conf = new JobConf(getConf(), ChainWordCount.class);
        conf.setJobName("wordcount");

        if (args.length != 2) {
            System.out.println("ERROR: Wrong number of parameters: " +
                    args.length + " instead of 2.");
            return printUsage();
        }
        FileInputFormat.setInputPaths(conf, args[0]);
        FileOutputFormat.setOutputPath(conf, new Path(args[1]));

        conf.setInputFormat(TextInputFormat.class);
        conf.setOutputFormat(TextOutputFormat.class);

        JobConf mapAConf = new JobConf(false);
        ChainMapper.addMapper(conf, Tokenizer.class, LongWritable.class, Text.class, Text.class, IntWritable.class, true, mapAConf);

        JobConf mapBConf = new JobConf(false);
        ChainMapper.addMapper(conf, LowerCaser.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, mapBConf);

        JobConf reduceConf = new JobConf(false);
        ChainReducer.setReducer(conf, Reduce.class, Text.class, IntWritable.class, Text.class, IntWritable.class, true, reduceConf);

        JobClient.runJob(conf);
        return 0;
    }

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new ChainWordCount(), args);
        System.exit(res);
    }
}
