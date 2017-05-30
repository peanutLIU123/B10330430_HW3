
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class rsa_final {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] length={512,1024,2048};
		
		char[] animal = {'c', 'a', 't'};
		BigInteger[] keep=new BigInteger[3];
		System.out.print("Plain text:cat");
		System.out.println();
		for(int j=0;j<3;j++){
		System.out.println("----------------------------------");
        System.out.println("****************************");			
		System.out.println("start of length"+length[j]*2+"\n");
			Bigint rsa=new Bigint(length[j]);
		BigInteger priKey=rsa.priK_d( rsa.get_e(),rsa.Fri_n() );
		//System.out.print("input one value to encription> ");
		Scanner input=new Scanner(System.in);
		
		//String in=input.nextLine();
		Cipher cipher=new Cipher(rsa);
		System.out.println("cipher:");
		for(int i=0;i<animal.length;i++){
			int temp=(int)animal[i];
			String in = Integer.toString(temp);
			BigInteger cipher_1=new BigInteger(cipher.to_cipher(in));
			System.out.print(cipher_1+"\n\n");
			
			keep[i]=cipher_1;
		}
		
		System.out.print("\nplaintext:");
			for(int i=0;i<animal.length;i++){
			int temp=CRT(keep[i],rsa.p,rsa.q,priKey).intValue();
			System.out.print((char)temp);
			
		}
		System.out.println();
		System.out.println("end of length"+length[j]*2+"\n");
		
        System.out.println("****************************");	
		System.out.println("-----------------------------------");
		System.out.println("\n");
		}
		
		
	}
	public static BigInteger CRT(BigInteger cipher,BigInteger p,BigInteger q,BigInteger d){
		BigInteger base=new BigInteger("1");
		BigInteger base2=new BigInteger("2");
		BigInteger exp1=d.mod(p.subtract(base));
		BigInteger exp2=d.mod(q.subtract(base));
		
		BigInteger m1=cipher.modPow(exp1, p);
		BigInteger m2=cipher.modPow(exp2, q);
		
		BigInteger power=p.subtract(base2);
		
		BigInteger inverse=q.modPow(power,p);
		
		if(m2.compareTo(m1)==1)m1.add(p);
		
		BigInteger k=(inverse.multiply(m1.subtract(m2))).mod(p);
		BigInteger plain=m2.add(q.multiply(k));
		return plain;
		
		
	}
	public static BigInteger pow(BigInteger cipher,BigInteger exp,BigInteger p){
		BigInteger y=cipher;
		BigInteger base=new BigInteger("1");
		BigInteger increase=new BigInteger("1");
		
		while(exp.compareTo(base)==1){
		    base=base.add(increase);
			y=y.multiply(cipher);
			
			y=y.mod(p);
			
		}
		return y;
	}
}
class Cipher{
	BigInteger pubN;
	BigInteger pubE;
	Cipher(){}
	Cipher(Bigint rsa){
		this.pubN=rsa.get_n();
		this.pubE=rsa.get_e();
	}
	public String to_cipher(String in){
		BigInteger plain=new BigInteger(in);
		primetest op=new primetest();
		return op.S_and_M(plain,this.pubE, this.pubN).toString();
	}
}
class Bigint{
	public BigInteger p,q;
	Bigint(int i){
		primetest ch_p=new primetest(i);
		primetest ch_q=new primetest(i);
		this.p=ch_p.Pprime(i);
		this.q=ch_q.Pprime(i);
	}
	Bigint(BigInteger a,BigInteger b){
		p=a;
		q=b;
	}
	public BigInteger get_n(){
		return this.p.multiply(this.q);
	}
	public BigInteger get_e(){
		String e="65537";
		return new BigInteger(e);
	}
	public BigInteger Fri_n(){
		BigInteger one=BigInteger.ONE;
		return (this.p.subtract(one)).multiply(this.q.subtract(one));
	}
	public BigInteger priK_d(BigInteger e,BigInteger FriN){
		BigInteger x=new BigInteger("0");
		BigInteger y=new BigInteger("1");
		return this.exgcd(e,FriN,x,y);
	}	
	public BigInteger exgcd(BigInteger e,BigInteger n,
							BigInteger x,BigInteger y){
		BigInteger x1=new BigInteger("0"),x0=new BigInteger("1") ,
				   y1=new BigInteger("1"),y0=new BigInteger("0");
		BigInteger r=e.mod(n),zero=BigInteger.ZERO,N=n;
		BigInteger q=(e.subtract(r)).divide(r);
		while(!r.equals(zero)){
			x=x0.subtract(q.multiply(x1));
			y=y0.subtract(q.multiply(y1));
			x0=x1; y0=y1;
			x1=x; y1=y;
			e=n; n=r; 
			r=e.mod(n);
			q=(e.subtract(r)).divide(n);
		}
		if(x.compareTo(zero)<0) x.add(N);
		return x;
	}
}
class primetest{
	char []prime;
	primetest(){ this.prime=new char[512];}
	primetest(int i){ this.prime=new char[i];}
	//primetest(char []prime){ this.prime=prime; }
	public BigInteger Pprime(int j){
		boolean test=false;
		String input=null;
		this.prime[0]='1';
		this.prime[j-1]='1';
		while(!test){
			for(int i=1;i<j-1;i++){ 
				if((int)(Math.random()*2)==0)this.prime[i]='0';
				else this.prime[i]='1';
			}
			input=new String(this.prime);
			if(this.Tprime(input)) test=true;
			else test=false;
		}
		return new BigInteger(input,2);
	}
	public boolean Tprime(String in){
		Random rnd=new Random();
		BigInteger N=new BigInteger(in,2);
		BigInteger one=BigInteger.ONE,two=new BigInteger("2");
		BigInteger zero=BigInteger.ZERO;
		BigInteger a=null;
		int k=0,i=0,cnt=0;
		BigInteger n=N.subtract(one);
		while(n.mod(two).equals(zero)){ 
			k++;
			n=n.divide(two);
		}
		BigInteger m=new BigInteger(n.toString());
		while(i<100){
			do{
				a=new BigInteger(N.bitLength(),rnd);	
			}while(a.compareTo(N.subtract(two))>0 || 
				   a.compareTo(two)<0);
			BigInteger b=this.S_and_M(a, m, N);	
			if( !b.equals(one) && !b.equals(N.subtract(one))){
				cnt=1;	 
				while(cnt<k && !b.equals( N.subtract(one) )){
					b=b.pow(2).mod(N);
					if(b.equals(one)) return false;
					cnt++;
				}
				if(!b.equals(N.subtract(one))) return false;
			}i++;
		}
		
		return true;
	}
	public BigInteger S_and_M(BigInteger x,BigInteger H,BigInteger n){
		String h=new String(H.toString(2));
		BigInteger y=x;
		for(int i=1;i<h.length();i++){
			y=y.pow(2).mod(n);
			if(h.charAt(i)=='1') y=(y.multiply(x)).mod(n);
		}
		return y;
	}
}