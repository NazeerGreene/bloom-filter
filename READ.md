a simple spell checker implemented with a bloom checker, inspired by: https://codingchallenges.fyi/challenges/challenge-bloom

### Step 1
In this step your goal is to use test driven development (TDD) to develop the Bloom filter data structure. OK you don’t have to use TDD, but I’d suggest writing some tests either before or after writing the Bloom filter, both to test that it works correctly and to help you understand how it works.

To implement a Bloom filter:

1. Determine the number of items that are likely to be stored and the probability of false positives you system can tolerate then use that to determine the memory requirements and number of has functions needed. There’s a formula, to use for this.
2. Create a bit array and set all bits to zero.
3. Insert items by applying the hash functions and setting the corresponding bits to one.
4. Query for the presence of an item by applying the hash functions. If any of the corresponding bits are zero, the item is definitely not in the set. If all of the bits are one, the item is probably in the set.

To determine the how many hash functions you should use and the interaction between the number of bits, the number of items and the number of hash functions there are a set of formulas which you’ll find documented on the Bloom filter Wikipedia page.

You can use hash functions from your programming languages standard library, implement your own or implement an existing one yourself. For the latter I suggest you check out the Fowler–Noll–Vo hash function and implement a couple of the versions of it. It’s a quick and easy hash function to implement if you’ve never written one before.

### Step 2
In this step your goal is to read the dictionary file and insert the words into the Bloom filter.

That might look like this:

```% ccspellcheck -build dict.txt```

And result in a file being saved that contains the dictionary. I called mine words.bf.

### Step 3
In this step your goal is to save the Bloom filter to disk. As the Bloom filter is binary we might like to add a simple header:

- The first four bytes will be an identifier, we’ll use CCBF.
- The next two bytes will be a version number to describe the version number of the file.
- The next two bytes will be the number of hash functions used.
- The next four bytes will be the number of bits used for the filter.

All in big endian. After that header, the bit array will be written out.

So when done, running the command from the previous step should result in a file being saved that contains the dictionary. I called mine words.bf.

You can use something like xxd to view a hex dump of the file and check your header, i.e.:
```
% xxd -l 32 words.bf
00000000: 4343 4246 0001 0004 003d 0900 6002 3104  CCBF.....=..`.1.
00000010: 0d40 2902 3095 0008 88a2 6010 0820 4201  .@).0.....`.. B.
```
Here we have the identifier, version 1, using 4 hash functions and 0x003d0900 bits.

Do note how small the saved Bloom filter file is compared to the input dictionary. For my implementation the numbers look like this:
```
% du -h dict.txt words.bf
2.4M    dict.txt
492K    words.bf
```

### Step 4
In this step your goal is to load the Bloom filter from disk. You’ll need to read the header - you should validate the file is of the type and version expected.

### Step 5
In this step your goal is to test words provided on the command line to see if they’re probably spelt right.
```
% ccpsellcheck hi hello word concurrency coding challenges imadethis up
These words are spelt wrong:
coding
challenges
imadethis
```
It’s disappointing that ‘coding’ and ‘challenges’ aren’t in the dictionary I picked for Coding Challenges, but there we go, life is like that! 🤷‍♂️.