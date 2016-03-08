<?php
include(dirname(__FILE__) . "/PhpBmp.php");
class Thumb {
    private $image;
    private $imageType;
    
    /**
     * 
     *加载图片
     * @param String $filename
     */
    public function load($filename) {
        $image_info = getimagesize($filename);
        if(!$image_info){
            return false;
        }        
        $this->imageType = $image_info[2];
        if( $this->imageType == IMAGETYPE_JPEG ) {
            $this->image = imagecreatefromjpeg($filename);
        } elseif( $this->imageType == IMAGETYPE_GIF ) {
            $this->image = imagecreatefromgif($filename);
        } elseif( $this->imageType == IMAGETYPE_PNG ) {
            $this->image = imagecreatefrompng($filename);
        } else if( $this->imageType == IMAGETYPE_BMP ) {
            $this->image = ImageCreateFromBMP($filename);
        }
        return true;
    }
    
    /**
     * 
     * 保存文件到给定的路径
     * @param String $filename
     * @param String $imageType
     * @param String $compression
     * @param String $permissions
     */
    public function save($filename, $imageType = IMAGETYPE_JPEG, $compression = 75) {
        $saveFlag = false;
        $imageType = $this->imageType;
        if( $imageType == IMAGETYPE_JPEG ) {
            $saveFlag = imagejpeg($this->image,$filename,$compression);
        } else if( $imageType == IMAGETYPE_GIF ) {
            $saveFlag = imagegif($this->image,$filename);
        } else if( $imageType == IMAGETYPE_PNG ) {
           $saveFlag =  imagepng($this->image,$filename);
        } else if( $imageType == IMAGETYPE_BMP ) {
           $saveFlag =  imagebmp($this->image,$filename);
        }		
        return $saveFlag;
    }

    /**
     * 
     * Enter description here ...
     */
    public function getWidth() {
        return imagesx($this->image);
    }
    
    /**
     * 
     * Enter description here ...
     */
    public function getHeight() {
        return imagesy($this->image);
    }
    
    /**
     * 
     * Enter description here ...
     * @param unknown_type $height
     */
    public function resizeToHeight($height) {
        $ratio = $height / $this->getHeight();
        $width = $this->getWidth() * $ratio;
        $this->resize($width,$height);
    }
    
    /**
     * 
     * Enter description here ...
     * @param unknown_type $width
     */
    public function resizeToWidth($width) {
        $ratio = $width / $this->getWidth();
        $height = $this->getheight() * $ratio;
        $this->resize($width,$height);
    }
    
    /**
     * 
     * 计算实际生成的缩略图的大小
     * @param int $scale
     */
    private function scale($scale) {
        $width = $this->getWidth() * $scale / 100;
        $height = $this->getheight() * $scale / 100;
        $this->resize($width,$height);
    }
    
    /**
     * 
     * Enter description here ...
     * @param unknown_type $width
     * @param unknown_type $height
     */
    private function resize($width,$height) {
        $newImage = imagecreatetruecolor($width, $height);
        imagealphablending($newImage, false);
        imagesavealpha($newImage, true);
        imagecopyresampled($newImage, $this->image, 0, 0, 0, 0, $width, $height, $this->getWidth(), $this->getHeight());
        $this->image = $newImage;
    }
    
    /**
     *
     * 返回图片文件类型的后缀名
     * @param String $filename
     */
    public function getImageTypeSuffix($filename) {
    	$image_info = getimagesize($filename);        
        $this->imageType = $image_info[2];
        if( $this->imageType == IMAGETYPE_JPEG ) {
            return "jpg";
        } elseif( $this->imageType == IMAGETYPE_GIF ) {
            return "gif";
        } elseif( $this->imageType == IMAGETYPE_PNG ) {
            return "png";
        } else if( $this->imageType == IMAGETYPE_BMP ) {
            return "bmp";
        } else {
        	return "";
        }
    }
}
?>