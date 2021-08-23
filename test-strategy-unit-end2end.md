# unit tests

First of all, they are called unitary because the are applied at the lowest logical scale of our solution: single methods, single classes, single sets of classes and methods.

They are unitary in the sense that we don't care about anything else but our test subject, in total isolation, without interference or influence, direct or indirect, from external components

Whenever our test subject interacts with external entities we need to simulate the external source in order to perform our test in a controlled environment

They are unitary in the sense that the test subject represents an atomic logical step in the solution without which the entire house of cards would crumble. They are the logical building steps of our solutions

So, the idea of unit tests is really to test the internal workings of our smaller atomic deployment units (atomic deployment units being those blobs of code and data that can be manipulated, replaced, rolled back, labeled, versioned, as a whole). Anything above this organic or systemic level, that is, at biological or ecosystemic level (think of examining your kidneys or your digestive system as opposed to measuring your general fitness or your hearing capabilities) falls into other categories, namely component, integration and end-to-end tests. The test subject of these tests-at-a-large-scope are exactly the atomic deployment units that can be manipulated, replaced, rolled back, labeled, versioned, etc., as a whole, by people other than coders

Thinking in terms of our analogy, in order to exam a kidney or the digestive system you may be required to do things like to fast completely, or to drink only water afterl midnight, or to eat more sugar than usual. These are called, in technical jargon, mockups. The comparisons are like fasting with a no response scenario; only water after midnight with a partial response in a time regulated fashion; more sugar then usual with data overflow.

So, in our application, what are the unit parts that need to work?

1. NonBlocking

   1. WebClient
      a. Webclient calls with 0 to 3 aggregation dimensionality (a bit of digression to highlight the importance of defining concepts and terminology: a call to the aggregation api is a request, which can have [0-3] dimension)

      1. Do 2D or 3D calls run in parallel?

   2. Data Wrapping and Unwraping by Client processors
      a. Multiples conversions happen between json, java and string
      b. Strings are wired data: request params, webclient bodies
      c. Json is returned by us to the user
      d. Java is the java incarnation of data
      e. Notice that our choice of using webclient implies checking if we correctly pack/unpack the Mono structure

      1. Java to String before calling backend - implicitly tested within webclient tests
      2. String to Java before returning result to controller

   3. Data Conversions
      a. Data cleanup: user request to valid request that is forwared to backend
      b. Data completion: ids filtered during data cleanup are restored to final result with null values

   4. Error Handling
      a. Severe: can't connect to backend, backend interruption
      b. Random: backend glitches (503), backend request timeout
      1. Note that the backend always returns a value for whatever idea
      2. The exception if pricing, which fails for numeric ids, but we are already covered by the data cleanup step
      3. Random errors are difficult to simulate because they originate on an external source. In order to test it properly we need to mockup the external source and inject our bad responses or bad moods (delays, crashes, etc.)

2. Throttled and Throttled with Time Limit (AS-2 and AS-3)

   1. Webclient call and data conversions already tested by the NonBlocking test cases

   2. Data Wrapping and Unwraping by Client processor and Error Handling are similar to the NonBlocking scenario, but the implementation differs slightly (we use the same techniques, for example Jackson mapper applied to String to Map, but the code is different). So we choose to rely on a combination of what has already been tested before with the supplement of end to end tests.

   3. So, the novel test cases that we need to perform are

      1. Enqueuing and Dequeuing

      2. Dequeuing and Caching

      3. Decaching and Request Completion

   a. Notice that by testing Dequeuing and Caching we are already testing the Caching Event mechanism

   b. If Dequeueing tests succeed and if end-to-end tests succeed, we know that Queue Event mechanism works

   c. By setting queue.time property > zero we trigger AS_3; if end-to-end tests succeed by any request dimensions containing less than queue.size ids, we know the timer mechanism works

From the above we see that different granularity levels of test cooperate with each other, avoid redundancy but at the some time entitling us with the necessary confidence in the quality of the solution

Finally, a crucial aspect of the solution is to satisfy the 99 percentile criteria. For that we gonna use stress tests, a form of end to end test on steroids: run external requests in parallel, minimum 100 and then multiples. We succeed if the difference between the beginning and the end of the test is less than 10 seconds.

