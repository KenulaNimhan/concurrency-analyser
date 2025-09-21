<h1>Overview</h1>

This codebase contains two main data structures, Stack and Queue implemented using different techniques.

<h2>Stack</h2>
<ul>
  <li>BasicStack     - simple array based stack which is not thread safe.</li>
  <li>NaiveSyncStack - array based stack with push and pop methods synchronized.</li>
  <li>SyncStack      - thread safe array based stack with wait(), notify() methods.</li>
  <li>LockBasedStack - array based stack made thread safe using ReentrantLock and Condition.</li>
  <li>LockFreeStack   - link list based stack which uses compare and swap (CAS) technique to make operations atomic.</li>
</ul>

<h2>Queue</h2>
<ul>
  <li>BasicQueue     - simple array based queue which is not thread safe.</li>
  <li>NaiveSyncQueue - array based queue with push and pop methods synchronized.</li>
  <li>SyncQueue      - thread safe array based queue with wait(), notify() methods.</li>
  <li>LockBasedQueue - array based queue made thread safe using ReentrantLock and Condition.</li>
  <li>LockFreeQueue - link list based queue which uses compare and swap (CAS).</li>
</ul>

<h1>How to Use</h1>

after cloning the application user can run Main.java,

user will be prompted to select the strucutre they want to test,

<ol>
  <li>Stack</li>
  <li>Queue</li>
</ol>

then user will be prompted to enter the following configurable data,
<br>
<ul>
  <li>Total number of elements to produce</li>
  <li>Number of producer threads</li>
  <li>Number of consumer threads</li>
  <li>Capacity of the stack / queue</li>
  <li>Size of the 'element' (object) being stacked / queued</li>
  <li>Scale of the estimated CPU usage per operation</li>
</ul>

<h3>Important Notes</h3>
* This application assumes that all the produced data must be consumed.
<br>
<br>
* All implementations are bounded, meaning they have a maximum capacity.
<br>
<br>
* The object that is being produced, and consumed is Element.java, which is a configurable object.
<br>
* Users can choose the size they want this object to be.
<br>
* User can enter a size between 1 - 512. A byte array of this size gets created as part of Element.java.
<br>
<br>
* Users can also choose the estimated compute power a pop or push might take (on a scale from 0-10).
<br>
* A compute operation takes place before each and every push/pop or enqueue/dequeue based on the scale configured. if user selects 10 all elements within byte array specified above will get accessed and incremented. Also a dummy math calculation takes place in order to mimic cpu usage.
<br>
* This is so the user can mimic the scenario they want in real life to get performance metrics accurate and practical as possible.

<h1>Metrics</h1>

the perfomance metrics will be displayed after all the implementations are tested for the configured data.
metrics include

<ul>
  <li>error count</li>
  <li>total time</li>
  <li>throughput</li>
  <li>average latency per thread</li>
</ul>
