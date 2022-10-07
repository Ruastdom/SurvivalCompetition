package xiamomc.survivalcompetition.misc;

public abstract class SingleInstanceObject
{
    private static SingleInstanceObject instance;

    protected SingleInstanceObject()
    {
        if (instance != null)
            throw new RuntimeException(this.getClass().getName() + "是单实例对象");

        instance = this;
    }
}
