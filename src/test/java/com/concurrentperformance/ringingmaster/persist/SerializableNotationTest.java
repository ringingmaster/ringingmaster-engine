package com.concurrentperformance.ringingmaster.persist;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import com.concurrentperformance.ringingmaster.engine.notation.NotationBody;
import com.concurrentperformance.ringingmaster.engine.notation.impl.NotationBuilder;

public class SerializableNotationTest {

	@Ignore //TODO
	@Test
	public void	canPersistAndReadSerializableFoldedPalindromeNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setFoldedPalindromeNotationShorthand("x18x18x18x18", "12");
		final NotationBody originalNotationBody = notationBuilder.build();

		checkPersistance(originalNotationBody);
	}

	@Ignore //TODO
	@Test
	public void	canPersistAndReadSerializableNotation() throws IOException, ClassNotFoundException {
		final NotationBuilder notationBuilder = NotationBuilder.getInstance();
		notationBuilder.setName("Plain Bob");
		notationBuilder.setUnfoldedNotationShorthand("x18x18x18x18");
		final NotationBody originalNotationBody = notationBuilder.build();

		checkPersistance(originalNotationBody);
	}

	private void checkPersistance(final NotationBody originalNotationBody)
			throws IOException, ClassNotFoundException {
		writeNotation(originalNotationBody);
		final NotationBody deserialisedNotationBody = readNotation();
		Assert.assertEquals(originalNotationBody.toString(), deserialisedNotationBody.toString());
	}

	private NotationBody readNotation( ) throws IOException, ClassNotFoundException {
		final NotationBody notationBody = null;
		/* TODO	FileInputStream fis = null;
		ObjectInputStream ois = null;
		SerializableNotation serializableNotation = null;
		try {
			fis =new FileInputStream("ringingmaster_method");
			ois = new ObjectInputStream(fis);
			serializableNotation = (SerializableNotation) ois.readObject();

			NotationBuilder notationBuilder1 = new NotationBuilder();
			notationBuilder1.setSerializableNotation(serializableNotation);
			notationBody = notationBuilder1.build();

		}
		finally {
			try {
				if (ois != null) {
					ois.close();
				}
			} catch (IOException e) {
			}
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
			}
		}*/
		return notationBody;
	}

	private void writeNotation(final NotationBody notationBody) throws IOException {
		/*TODO 	SerializableNotation serializableNotation = notationBody.toSerializableNotation();

		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream("ringingmaster_method");
			oos = new ObjectOutputStream(fos);
			oos.writeObject(serializableNotation);
	    }
		finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}


}
