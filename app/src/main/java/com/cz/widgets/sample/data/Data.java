package com.cz.widgets.sample.data;

import java.util.Random;

public class Data {
    public final static String[] IMAGE_ARRAY=new String[]{
            "https://imgsa.baidu.com/forum/w%3D580/sign=930d66414634970a47731027a5cbd1c0/59310a55b319ebc48c9bd8f78526cffc1f1716f8.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=77e8349d7e310a55c424defc87444387/b822720e0cf3d7cafb159118f51fbe096a63a9f9.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=d0ef968f65d9f2d3201124e799ec8a53/b12397dda144ad34f2992ee9d7a20cf431ad85be.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=890492aa1cd5ad6eaaf964e2b1cb39a3/3f30e924b899a9011717f2581a950a7b0208f5ba.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=587cc5dcb5b7d0a27bc90495fbee760d/6134970a304e251f60824d96a086c9177f3e5363.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=4c8bd6b329738bd4c421b239918a876c/233b5bb5c9ea15cea0ec4967b1003af33a87b25a.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=fc0748742f34349b74066e8df9ea1521/c0fe9925bc315c60ed82c2818ab1cb134954774d.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=7e712c20cd8065387beaa41ba7dca115/39d8bc3eb13533fa6fe93a40afd3fd1f41345b05.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=00e7255f4d90f60304b09c4f0913b370/2c338744ebf81a4cdb6a45a7d02a6059252da600.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=a65efe87f5d3572c66e29cd4ba126352/b7dda144ad3459821e384cfc0bf431adcbef842a.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=d6896b5d968fa0ec7fc764051696594a/ebef76094b36acaf9a843f927bd98d1001e99c7c.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=91505fa3fb039245a1b5e107b795a4a8/abd4b31c8701a18bd548bb21992f07082838fe39.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=aa76ac10221f95caa6f592bef9167fc5/cbc4b74543a98226f28082498d82b9014a90eb1f.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=427d0dd1a7ec08fa260013af69ef3d4d/56094b36acaf2edd16b2e0878a1001e939019319.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=0003255f4d90f60304b09c4f0913b370/2c338744ebf81a4cdb8e45a7d02a6059252da624.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=d077968f65d9f2d3201124e799ed8a53/b12397dda144ad34f2012ee9d7a20cf431ad8526.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=a17535b28d13632715edc23ba18ea056/92b7d0a20cf431ad7b45ea574c36acaf2fdd98cd.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=ea57330ff4deb48ffb69a1d6c01e3aef/8d6eddc451da81cb2e762aa35566d016082431cf.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=ecd2d50a0e23dd542173a760e108b3df/2fb30f2442a7d933ef544d1daa4bd11372f001db.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=32317ca09e25bc312b5d01906ede8de7/2924ab18972bd4072a3e7a937c899e510eb309e6.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=646f3d1aaa345982c58ae59a3cf5310b/88d3fd1f4134970ac3fcb94192cad1c8a6865dd5.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=fe4351e16b81800a6ee58906813533d6/a2025aafa40f4bfbd4f6363a044f78f0f73618bd.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=b31e52459a16fdfad86cc6e6848e8cea/f21b0ef41bd5ad6ec935cd8486cb39dbb7fd3c8b.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=f181504f758b4710ce2ffdc4f3cec3b2/d8198618367adab401cdb32d8cd4b31c8701e4bb.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=d1f76a5d968fa0ec7fc764051697594a/ebef76094b36acaf9dfa3e927bd98d1001e99c42.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=b371060e48c2d562f208d0e5d71090f3/e3cec3fdfc039245dcc149618094a4c27d1e2563.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=1bf2345f4b086e066aa83f4332097b5a/dbf2b2119313b07e69ebf8660bd7912397dd8c0e.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=dabc0048fafaaf5184e381b7bc5594ed/2ef41bd5ad6eddc4457d1c953edbb6fd536633c1.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=7fe4e5aef036afc30e0c3f6d8318eb85/29f790529822720e70584a947ccb0a46f31fabce.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=8d93d668abaf2eddd4f149e1bd110102/e9177f3e6709c93d8b55a699983df8dcd100543d.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=2aa92160b63533faf5b6932698d2fdca/dbedab64034f78f0c447329d7e310a55b3191c0b.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=dffdc6ebceea15ce41eee00186013a25/14a85edf8db1cb139592947dda54564e93584be1.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=1907174d642762d0803ea4b790ed0849/f0a20cf431adcbefc6e2d668abaf2edda2cc9f8e.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=7d7caaf659df8db1bc2e7c6c3923dddb/15fae6cd7b899e512eaf917a45a7d933c8950dbe.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=14d28b9031d3d539c13d0fcb0a86e927/adb1cb1349540923006cc8109558d109b3de4953.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=cbe20e0c9f22720e7bcee2f24bcb0a3a/99389b504fc2d56247207b83e01190ef76c66cb0.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=8a1e2d47ecc4b7453494b71efffd1e78/f7ca7bcb0a46f21f3e42f53df1246b600c33ae6e.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=3190b170be389b5038ffe05ab534e5f1/9912c8fcc3cec3fdfb420c1bd188d43f8794275b.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=e86d0eb171c6a7efb926a82ecdfbafe9/a794a4c27d1ed21b0188858baa6eddc451da3f1e.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=b06c050e48c2d562f208d0e5d71090f3/e3cec3fdfc039245dfdc4a618094a4c27d1e2576.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=8746cba5b5fb43161a1f7a7210a54642/21e93901213fb80ec1c7cab931d12f2eb938944e.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=995514dcbe014a90813e46b599763971/4b600c338744ebf89d0ecf51def9d72a6059a765.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=f77acd1dfcf2b211e42e8546fa816511/f158ccbf6c81800afe9a2260b63533fa828b4765.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=accc120a93eef01f4d1418cdd0ff99e0/a1cb39dbb6fd5266c855977aac18972bd4073665.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=254b4c4509f41bd5da53e8fc61db81a0/08381f30e924b899f154d25669061d950a7bf670.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=d3f13d927bd98d1076d40c39113eb807/e93d70cf3bc79f3d05f04f5ebda1cd11728b294f.jpg",
            "https://imgsa.baidu.com/forum/w%3D580/sign=fb89aff3ccef76093c0b99971edca301/b5cad1c8a786c9178843f957ce3d70cf3bc75718.jpg"
    };

    public static String getImage(){
        final Random random=new Random();
        int index = random.nextInt(IMAGE_ARRAY.length);
        return IMAGE_ARRAY[index];
    }
}
