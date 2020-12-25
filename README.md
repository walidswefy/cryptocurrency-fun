Cryptocurrency for fun!
====================

## What is that?

A simulation to a decentralized cryptocurrency built using blockchain technology, where transactions are securely stored in a distributed ledger.

The implementation is meant to give a better understanding of how crytocurrencies work in practise. Individual miners are racing to solve a cryptographic challenge and get rewards for that (fun run!), and the network of miners employs a consensus mechanism (proof-of-work) to prevent the double spend problem.
 
## Comparing to Bitcoin

### Similarities
* Users use stored public/private keys in wallets to manage their accounts.
* Transactions are signed using wallet's private key. Network verifies transactions using user's shared public key.
* Transactions propagate within the network of miners awaiting confirmation.
* Miners are racing to add unconfirmed transactions to the chain of blocks.
* A distributed chain stores the miners' blocks after validation, thus confirming all the block transactions.
* Miners receive awards for adding a new block and getting fees on confirmed transactions.
* Miners can add empty blocks if no transactions are unconfirmed.
* Genesis block (the first block in the chain) has no actual transactions.
* Coinbase is the first transaction in any block that grants the miner its rewards.

### Differences
* implementation relies on "Hazelcast"; a distributed computing framework where nodes run in a local network.
* Hazelcast internally uses Raft consensus protocol for implementing distributed systems.
* Unconfirmed transactions, potential and confirmed blocks propagate to all miners using distributed publish-subscribe model.
* The master node manages a voting between all miners to confirm the block.
* Distributed list manages the public block chain. Blocks are replicated between nodes.
* For simplicity, award amounts are fixed, and wallets have an infinite balances!
* Wallet addresses are encoded in hexadecimal, whereas Bitcoin uses Base58 for that.
* Hashing done on concatenated block data, whereas Bitcoin uses Merkle tree for Simple Payment Verification (SPV). 
 
## Technology Stack

- `java`, `spring-boot`
  [core]
- `hazelcast`
  [distributed computing]  
- `git`, `maven`
  [code & build]
- `logback`, `filebeat`, `elastic-search`, `kibana`
  [log management]
- `lombok`, `devtools`
  [development] 
- `docker`, `docker-compose`
  [containers]

## How to run

compile application:

```bash
mvn clean package
```

run multiple miners, single wallet for posting transactions

```bash
# thin client wallet
java -jar target/*.jar

# full minning node
java -jar -Dspring.profiles.active=miner target/*.jar
java -jar -Dspring.profiles.active=miner target/*.jar
```

build docker image:

```bash
mvn com.google.cloud.tools:jib-maven-plugin:2.4.0:dockerBuild
```

### application profiles

* **default**: thin client (wallet)
* **miner**:  exhaustive miner application (Mining and verifying transactions and blocks)
