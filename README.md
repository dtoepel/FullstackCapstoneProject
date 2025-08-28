The election manager allows the user to manage elections, candidates and voters.

https://electionmanagement-55ha.onrender.com

# Elections

Select an id, name, description, the candidate type, election method and number of seats. 
A newly created election (and an election during creation) has status OPEN. 
Only in this state, candidates can be edited. 
Only candidates with a matching type can be added.  
The order of candidates can be edited. It has usually no further effect beyond the order of candidates on the ballot.

The default method is Single-Transferable-Vote. 
When only one seat is to be distributed, this defaults to an Instant-Runoff-Election.

The status can then be advanced to VOTING. 
Until voter identification is implemented, a vote can be cast by clicking the "Vote" Button.
The voter can add and rank any number of candidates by preference. 
The most preferred candidate should be at the top, 
the candidate least acceptable should be at the bottom, 
and the unacceptable candidates should be left unranked. 

The election can then be advanced to CLOSED. 
Voting is no longer possible, and the election can be counted.

# Candidates
Candidates have a name, party, color, description and a type.
The color must be a 3 or 6 digit hexadecimal number.
Name, party and description can be freely chosen and have no further effect.
The default type is Person, but other types can be defined, such as holiday destinations or the menu for a BBQ. 
This field is a simple String. A candidate can be added to an election, when the type matches.

# Voter
A voter has a list of elections, in which the voter can still cast a vote. 
The voter has a code, which is needed to validate the vote

# Features

## Basic
Elections and Candidates available. Election modes STV + variations. No voter registration, vote open for all.
Elections and Candidates have type for appropriate filtering.

## Archive 
Status archived for elections => filter on dashboard
Status archived for candidates => no longer allow adding to elections

## Voter Management
Mock a voter management. In theory, emails are sent to the voters containing a link and an activation code. 
Practically, on advancing an election to VOTING, a number of voters is supplied. Then a list of voters is generated, 
the voters properties are an activation code, the election it is bound to, and whether the user has already voted. 
The latter can be done implicitly by removing the activation code on voting, or removing the voter entirely.

## Other Voting Methods

Simple variations: 
 * FPTP: only one candidate to be voted for, only one seat to be filled
 * AV: only one or two candidates to be voted for, only one seat to be filled
 * Condorcet: Other methods to find the winner

## More Detailed Count Result

Log for each round, who has been elected/eliminated and how the votes are distributed

