
package jb.selfregulation.impl.proteinfolding.problem;

public class InvalidModelException extends Exception
{
    int collisionLength;
    byte [][] model;
    
    public InvalidModelException(
            String msg, 
            int aLength, 
            byte [][] aModel)
    {
        super(msg);
        collisionLength = aLength;
        model = aModel;
    }

    public int getCollisionLength()
    {
        return collisionLength;
    }

    public void setCollisionLength(int collisionLength)
    {
        this.collisionLength = collisionLength;
    }

    public byte[][] getModel()
    {
        return model;
    }

    public void setModel(byte[][] model)
    {
        this.model = model;
    }
    
    
}
