# Demonstrate CJ Fintech Batch

Code to excercise features of the batch library.

The demo puts results to `stdout`. The lines starting with 'LOG' are
effects of the `log(..)` function "invoked" from within the for
comprehension. The rejection report comes from the results of the
process.

```text
LOG: 1: commission 76.2789 posted to publisher 144 from advertiser 2 for order x29059
LOG: 4: commission 285.7203 posted to publisher 271 from advertiser 2 for order A00034
LOG: 7: commission 13.1196 posted to publisher 1 from advertiser 0 for order qx49cc-6
rejected: 
  2: 'InvalidAmount  (z390,     1013, -9.12,  254)
  3: 'InvalidContract  (A00033,   1900, 1.01,  80)
  5: 'Parse  (how did this get in here?)
  6: 'Line6  (qx49cc-5, 12,   303.73, 150)
  8: 'BlankLine  (    )
```
