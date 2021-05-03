# Camellia-Cipher
Algorithm Implementation in Java

## Description
Camellia is a symmetric-key data encryption algorithm. The block size is 128 bits, the key can consist of 128, 192 or 256 bits. 
Detailed description of the algorithm may be found in [RFC 3713 - A Description of the Camellia Encryption Algorithm](https://tools.ietf.org/html/rfc3713)

## Files
Camellia.java - contains the class that implements Camellia algorithm.

Camellia_Test.java - contains a testing class that performs encryption of reference values from RFC 3713 and encryption and decryption of 5MB file for measuring time of work purpose.

## Usage
Import Camellia.java into your project and create an object of class Camellia. Than you can use Encrypt and Decrypt methods, that take file path and 
a key as arguments. After encryption process is finished, a file with extension ".crptd" will appear in the folder where original file is located. Decryption works
the same way.

`public void Encrypt(String path, String key)`  
`public void Decrypt(String path, String key)`
