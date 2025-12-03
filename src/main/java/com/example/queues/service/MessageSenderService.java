package com.example.queues.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@Service
public class MessageSenderService {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("#{'${artemis.queues}'.split(',')}")
    private List<String> queueNames;

    // Cache for optimized word storage - LinkedHashSet maintains insertion order and eliminates duplicates
    private volatile Set<String> wordsCache = null;
    
    // Lazy-loaded content cache with replacements applied
    private volatile String processedContentCache = null;
    
    // Pre-
    private static final Pattern WORD_SPLIT_PATTERN = Pattern.compile("\\s+");
    
    // Optimized replacement map for O(1) lookups instead of multiple regex operations
    private static final Map<String, String> WORD_REPLACEMENTS = new HashMap<>();
    
    static {
        // Initialize replacement mappings - case-insensitive keys stored in lowercase
        WORD_REPLACEMENTS.put("angular", "AnGular");
        WORD_REPLACEMENTS.put("aws", "OpenShift");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void sendSampleMessages() {
        System.out.println("Application is ready. Sending sample messages to all queues...");
        // Call both replacement functions for timing demonstration
        readAndReplaceWordsFromFile();
        readAndReplaceWordsFromFileLazy();
        // for (String queueName : queueNames) {
        //     sendMessageToQueue(queueName);
        // }
    }

    private void sendMessageToQueue(String queueName) {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String message = String.format("Sample message to %s at %s", queueName, timestamp);
            Set<String> words = getOptimizedWordsFromFile();
            if (!words.isEmpty()) {
                message = String.join(" ", words);
            } 
            jmsTemplate.convertAndSend(queueName, message);
            System.out.println("Sent message to " + queueName + ": " + message);
        } catch (Exception e) {
            System.err.println("Error sending message to " + queueName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendCustomMessage(String queueName, String message) {
        try {
            
            jmsTemplate.convertAndSend(queueName, message);
            System.out.println("Sent custom message to " + queueName + ": " + message);
        } catch (Exception e) {
            System.err.println("Error sending custom message to " + queueName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Gets words from file using optimized caching and data structure.
     * Uses TreeSet for automatic alphabetical sorting, O(log n) lookups, and duplicate elimination.
     */
    public Set<String> getOptimizedWordsFromFile() {
        // Double-checked locking pattern for thread-safe lazy initialization
        if (wordsCache == null) {
            synchronized (this) {
                if (wordsCache == null) {
                    wordsCache = loadAndProcessWords();
                }
            }
        }
        return wordsCache;
    }

    private Set<String> loadAndProcessWords() {
        try {
            ClassPathResource resource = new ClassPathResource("Mital Parikh.txt");
            if (!resource.exists()) {
                System.err.println("File 'Mital Parikh.txt' not found in classpath");
                return new LinkedHashSet<>();
            }
            
            // Read entire file content efficiently
            String content = Files.readString(Path.of(resource.getURI()));
            
            // Split by whitespace and collect into LinkedHashSet
            String[] wordsArray = WORD_SPLIT_PATTERN.split(content.trim());
            
            // TreeSet provides:
            // - Automatic alphabetical sorting (Red-Black Tree - O(log n) operations)
            // - Automatic duplicate elimination
            // - Sorted iteration (alphabetical order)
            // - Memory efficient for large datasets with duplicates
            Set<String> words = new TreeSet<>(Arrays.asList(wordsArray));
            
            System.out.println("Loaded " + words.size() + " unique words from file (alphabetically sorted)");
            return words;
            
        } catch (IOException e) {
            System.err.println("Error reading 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashSet<>();
        } catch (Exception e) {
            System.err.println("Unexpected error processing 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashSet<>();
        }
    }

    /**
     * Legacy method - kept for backward compatibility.
     * Consider using getOptimizedWordsFromFile() for better performance.
     */
    public String[] readAndSplitWordsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource("Mital Parikh.txt");
            if (!resource.exists()) {
                System.err.println("File 'Mital Parikh.txt' not found in classpath");
                return new String[0];
            }
            
            // Read entire file content efficiently
            String content = Files.readString(Path.of(resource.getURI()));
            
            // Split by whitespace and filter out empty strings
            String[] words = WORD_SPLIT_PATTERN.split(content.trim());
            
            // Sort words in-place for optimal performance (uses TimSort - O(n log n))
            Arrays.sort(words);
            
            return words;
            
        } catch (IOException e) {
            System.err.println("Error reading 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return new String[0];
        } catch (Exception e) {
            System.err.println("Unexpected error processing 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * Reads content from "Mital Parikh.txt" file and performs word replacements.
     * Uses optimized HashMap-based word replacement for O(1) lookups instead of regex.
     * Replaces "angular" with "AnGular" and "AWS" with "OpenShift" (case-insensitive).
     * Returns the modified content as a string.
     */
    public String readAndReplaceWordsFromFile() {
        long start = System.currentTimeMillis();
        try {
            ClassPathResource resource = new ClassPathResource("Mital Parikh.txt");
            if (!resource.exists()) {
                System.err.println("File 'Mital Parikh.txt' not found in classpath");
                return "";
            }
            
            // Read entire file content efficiently
            String content = Files.readString(Path.of(resource.getURI()));
            
            // Split content into words for optimized processing
            String[] words = WORD_SPLIT_PATTERN.split(content);
            
            // Use StringBuilder for efficient string concatenation - O(n) instead of O(n²)
            StringBuilder modifiedContent = new StringBuilder(content.length());
            
            for (int i = 0; i < words.length; i++) {
                String word = words[i];
                String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(); // Remove punctuation for lookup
                
                // O(1) HashMap lookup instead of O(m) regex operations
                String replacement = WORD_REPLACEMENTS.get(cleanWord);
                if (replacement != null) {
                    // Preserve original punctuation/formatting
                    String originalPunctuation = word.replaceAll("[a-zA-Z0-9]", "");
                    modifiedContent.append(replacement).append(originalPunctuation);
                } else {
                    modifiedContent.append(word);
                }
                
                // Add space between words (except for last word)
                if (i < words.length - 1) {
                    modifiedContent.append(" ");
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("Content processed with optimized word replacements: angular -> AnGular, AWS -> OpenShift");
            System.out.println("Execution time (readAndReplaceWordsFromFile): " + (end - start) + " ms");
            return modifiedContent.toString();
            
        } catch (IOException e) {
            System.err.println("Error reading 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            System.err.println("Unexpected error processing 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Lazy-loaded, optimized word replacement function with caching.
     * Uses Stream API for memory-efficient processing and caches result.
     * Replaces "angular" with "AnGular" and "AWS" with "OpenShift" (case-insensitive).
     * Returns the modified content as a string with lazy evaluation.
     */
    public String readAndReplaceWordsFromFileLazy() {
        long start = System.currentTimeMillis();
        // Double-checked locking pattern for thread-safe lazy initialization
        if (processedContentCache == null) {
            synchronized (this) {
                if (processedContentCache == null) {
                    processedContentCache = loadAndProcessContentLazily();
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Execution time (readAndReplaceWordsFromFileLazy): " + (end - start) + " ms");
        return processedContentCache;
    }

    private String loadAndProcessContentLazily() {
        try {
            ClassPathResource resource = new ClassPathResource("Mital Parikh.txt");
            if (!resource.exists()) {
                System.err.println("File 'Mital Parikh.txt' not found in classpath");
                return "";
            }
            
            // Lazy stream processing - reads and processes line by line for memory efficiency
            String result = Files.lines(resource.getFile().toPath(), StandardCharsets.UTF_8)
                .parallel() // Parallel processing for large files
                .map(this::processLineWithReplacements)
                .collect(StringBuilder::new,
                    (sb, line) -> {
                        if (sb.length() > 0) sb.append(System.lineSeparator());
                        sb.append(line);
                    },
                    (sb1, sb2) -> {
                        if (sb1.length() > 0) sb1.append(System.lineSeparator());
                        sb1.append(sb2);
                    })
                .toString();
            
            System.out.println("Content processed lazily with optimized word replacements: angular -> AnGular, AWS -> OpenShift");
            return result;
            
        } catch (IOException e) {
            System.err.println("Error reading 'Mital Parikh.txt' lazily: " + e.getMessage());
            e.printStackTrace();
            return "";
        } catch (Exception e) {
            System.err.println("Unexpected error in lazy processing of 'Mital Parikh.txt': " + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Processes a single line with word replacements using optimized approach.
     * Uses pre-compiled patterns and HashMap for O(1) lookups.
     */
    private String processLineWithReplacements(String line) {
        if (line == null || line.trim().isEmpty()) {
            return line;
        }
        
        String[] words = WORD_SPLIT_PATTERN.split(line);
        StringBuilder processedLine = new StringBuilder(line.length());
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            String cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            
            // O(1) HashMap lookup
            String replacement = WORD_REPLACEMENTS.get(cleanWord);
            if (replacement != null) {
                // Preserve original punctuation/formatting
                String originalPunctuation = word.replaceAll("[a-zA-Z0-9]", "");
                processedLine.append(replacement).append(originalPunctuation);
            } else {
                processedLine.append(word);
            }
            
            // Add space between words (except for last word)
            if (i < words.length - 1) {
                processedLine.append(" ");
            }
        }
        
        return processedLine.toString();
    }
}
