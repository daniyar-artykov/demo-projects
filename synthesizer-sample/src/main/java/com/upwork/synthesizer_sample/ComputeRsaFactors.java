package com.upwork.synthesizer_sample;

import java.math.BigInteger;
import java.util.Random;

/**
 * Computes the factors of n given d and e.
 * Given are the public RSA key (n,d)
 * and the corresponding private RSA key (n,e).
 */
public class ComputeRsaFactors
{
    /**
     * Executes the program.
     *
     * @param args  The command line arguments.
     */
    public static void main(String[] args)
    {
        final BigInteger n = BigInteger.valueOf(10142789312725007L);
        final BigInteger d = BigInteger.valueOf(5);
        final BigInteger e = BigInteger.valueOf(8114231289041741L);

        final long t0 = System.currentTimeMillis();

        final BigInteger kTheta = d.multiply(e).subtract(BigInteger.ONE);
        final int exponentOfTwo = kTheta.getLowestSetBit();

        final Random random = new Random();
        BigInteger factor = BigInteger.ONE;
        System.out.println(t0);
        do
        {
            final BigInteger a = nextA(n, random);

            for (int i = 1; i <= exponentOfTwo; i++)
            {
                final BigInteger exponent = kTheta.shiftRight(i);
                final BigInteger power = a.modPow(exponent, n);

                final BigInteger gcd = n.gcd(power.subtract(BigInteger.ONE));
                if (!factor.equals(BigInteger.ONE))
                {
                    break;
                }
            }
        }
        while (factor.equals(BigInteger.ONE));

        final long t1 = System.currentTimeMillis();

        System.out.printf("%s %s (%dms)\n", factor, n.divide(factor), t1 - t0);
    }


    private static BigInteger nextA(final BigInteger n, final Random random)
    {
        BigInteger r;
        do
        {
            r = new BigInteger(n.bitLength(), random);
        }
        while (r.signum() == 0 || r.compareTo(n) >= 0);
        return r;
    }
}