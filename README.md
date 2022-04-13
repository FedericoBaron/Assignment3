# Assignment3

## Problem 1: The Birthday Presents Party (50 points)

The Minotaur’s birthday party was a success. The Minotaur received a lot of presents from his guests. The next day he decided to sort all of his presents and start writing “Thank you” cards. Every present had a tag with a unique number that was associated with the guest who gave it. Initially all of the presents were thrown into a large bag with no particular order. The Minotaur wanted to take the presents from this unordered bag and create a chain of presents hooked to each other with special links (similar to storing elements in a linked-list). In this chain (linked-list) all of the presents had to be ordered according to their tag numbers in increasing order. The Minotaur asked 4 of his servants to help him with creating the chain of presents and writing the cards to his guests. Each servant would do one of three actions in no particular order:

Take a present from the unordered bag and add it to the chain in the correct location by hooking it to the predecessor’s link. The servant also had to make sure that the newly added present is also linked with the next present in the chain.
Write a “Thank you” card to a guest and remove the present from the chain. To do so, a servant had to unlink the gift from its predecessor and make sure to connect the predecessor’s link with the next gift in the chain.
Per the Minotaur’s request, check whether a gift with a particular tag was present in the chain or not; without adding or removing a new gift, a servant would scan through the chain and check whether a gift with a particular tag is already added to the ordered chain of gifts or not.
As the Minotaur was impatient to get this task done quickly, he instructed his servants not to wait until all of the presents from the unordered bag are placed in the chain of linked and ordered presents. Instead, every servant was asked to alternate adding gifts to the ordered chain and writing “Thank you” cards. The servants were asked not to stop or even take a break until the task of writing cards to all of the Minotaur’s guests was complete.

After spending an entire day on this task the bag of unordered presents and the chain of ordered presents were both finally empty!

Unfortunately, the servants realized at the end of the day that they had more presents than “Thank you” notes. What could have gone wrong?

Can we help the Minotaur and his servants improve their strategy for writing “Thank you” notes?

Design and implement a concurrent linked-list that can help the Minotaur’s 4 servants with this task. In your test, simulate this concurrent “Thank you” card writing scenario by dedicating 1 thread per servant and assuming that the Minotaur received 500,000 presents from his guests.

### Strategy:

For this problem, I decided to have each servant take a present and then write a thank you note in a loop, all while checking if the minotaur made a request. The concurrent linked list I chose to use is the optimistic approach, as it is very efficient and thought it would work well for this assignment. The items get removed from the bag and added to the list, and the thank you notes are written when we remove an gift from the front of the list. Finally, we use the contains method in the list to satisfy the requests the minotaur makes to see whether a particular gift is in the linked list or not.

The reason why someone could end up with more gifts than thank you notes is if they don't write a validation step in the linked list to validate that we succeeded in adding/removing the gift to/from the linkedlist.

### Correctness, Efficiency and Evaluation:

The program is verified to write 500,000 thank you notes and to add 500,000 gifts into the linked list, all while checking whether a gift is in the list at the minotaurs request. Overall I'm happy with the implementation as I believe it is fairly clean and concise codewise as well as efficient.

The big o runtime of this program is O(N), with N being the number of presents which in this case is 500,000. 

### How to run

To run this program:
  - clone the repository
  - open your terminal
  - cd into the Assignment3 folder
  - cd into the Program1 folder
  - type javac pa3cop4520p1.java
  - type java pa3cop4520p1

## Problem 2: Atmospheric Temperature Reading Module (50 points)

You are tasked with the design of the module responsible for measuring the atmospheric temperature of the next generation Mars Rover, equipped with a multi-core CPU and 8 temperature sensors. The sensors are responsible for collecting temperature readings at regular intervals and storing them in shared memory space. The atmospheric temperature module has to compile a report at the end of every hour, comprising the top 5 highest temperatures recorded for that hour, the top 5 lowest temperatures recorded for that hour, and the 10-minute interval of time when the largest temperature difference was observed. The data storage and retrieval of the shared memory region must be carefully handled, as we do not want to delay a sensor and miss the interval of time when it is supposed to conduct temperature reading. 

Design and implement a solution using 8 threads that will offer a solution for this task. Assume that the temperature readings are taken every 1 minute. In your solution, simulate the operation of the temperature reading sensor by generating a random number from -100F to 70F at every reading. In your report, discuss the efficiency, correctness, and progress guarantee of your program.

### Strategy:

For this program I decided to use PriorityBlockingQueue which is a heap library that is thread-safe to handle the highest and lowest temperatures. What I do is I use a maxHeap to hold the 5 lowest temperatures by adding to the heap until we reach size 6 and then remove the head (which is the highest temperature in that heap). To get the highest temperatures I use the same approach but with a minHeap instead. This approach saves memory as we only ever hold up to 6 temperatures at a time for each, and it is efficient since all we ever have to do is remove the head of a really small heap, or add to a really small heap. To get the highest temperature difference at an interval I use a concurrent optimistic linked list that keeps all the highest and lowest temperatures at each minute. Then I do a for loop where I go through all the possible intervals and get the highest difference of temperatures in each one. I store the interval with the highest difference and print that one after we iterate through all of the possible intervals.

### Correctness, Efficiency and Evaluation:

This program is thread safe as I use both a concurrent linked list and a thread-safe heap. The efficiency of the program was taken into account by using the minHeap and maxHeap which end up being very efficent, as well as an optimistic linked list. 

The interval is found with a runtime O(N), N being the number of reports we have to make. The highest and lowest temperatures are Nlog(N), again with N being the number of reports we have to make. This is because adding to a heap and removing from a heap can be Log(N). Therefore the overall runtime of the program is Nlog(N)

### How to run

To run this program:
  - clone the repository
  - open your terminal
  - cd into the Assignment3 folder
  - cd into the Program2 folder
  - type javac pa3cop4520p2.java
  - type java pa3cop4520p2
